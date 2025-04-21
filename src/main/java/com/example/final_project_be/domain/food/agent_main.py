"""
식사 에이전트 메인 모듈
"""

from typing import Dict, Any, List, Optional, Tuple, Annotated
from langchain_openai import ChatOpenAI
from pydantic import BaseModel, Field
from langchain_core.messages import AIMessage, HumanMessage
from langchain_core.prompts import ChatPromptTemplate
from langchain.callbacks.manager import CallbackManager
from langchain.tools import tool, Tool
import json
import os
from dataclasses import dataclass
from enum import Enum, auto
from langchain.agents import AgentExecutor, create_tool_calling_agent
from .workflow import run_food_workflow
from .food_agent import FoodAgent

# LangSmith 설정
os.environ["LANGCHAIN_TRACING_V2"] = "true"
os.environ["LANGCHAIN_PROJECT"] = "food-agent"

FOOD_AGENT_PROMPT = """
당신은 식단 관리 에이전트입니다. 사용자의 입력을 분석하여 아래 항목들을 JSON 형식으로 반환하세요:

- intent: 사용자의 의도
  - 예시: "meal_input" (식사 입력), "meal_recommendation" (식단 추천), "nutrient_analysis" (영양 분석)
  - 추가적으로 사용자의 요청에 따라 다양한 의도를 분석할 수 있습니다.
- meal_type: 식사 유형 (가능하면 추론, 없으면 "unknown")
  - 예시: "breakfast", "lunch", "dinner", "snack", "unknown"
  - 사용자가 특정한 식사 유형을 언급하지 않으면 "unknown"으로 처리합니다.
- type: 항상 "food"로 설정
  - 이 항목은 항상 "food"로 반환되어야 합니다.

🎯 출력 형식은 반드시 아래 예시처럼 JSON으로 응답하세요:

```json
{
  "intent": "meal_input",
  "meal_type": "lunch",
  "type": "food"
}

```

"""

class IntentType(Enum):
    """
    의도 유형 정의
    """
    MEAL_INPUT = "meal_input"
    MEAL_RECOMMENDATION = "meal_recommendation"
    NUTRIENT_ANALYSIS = "nutrient_analysis"
    FOOD_SEARCH = "food_search"
    UNKNOWN = "unknown"

async def run_food_agent(message: str, user_id: int = 1) -> Dict[str, Any]:
    """
    식사 에이전트를 실행합니다.
    
    Args:
        message: 사용자 메시지
        user_id: 사용자 ID
        
    Returns:
        처리 결과
    """
    try:
        agent = FoodAgent()
        return await agent.process(message, user_id)
        
    except Exception as e:
        return {
            "type": "food",
            "response": "죄송합니다. 처리 중 오류가 발생했습니다.",
            "error": str(e)
        }
    
    def process(self, message: str, user_id: str = '1', chat_history: List[Dict[str, str]] = None) -> Dict[str, Any]:
        """
        사용자 메시지를 처리합니다. (이전 버전과의 호환성을 위해 유지)
        
        Args:
            message: 사용자 메시지
            user_id: 사용자 ID (문자열)
            chat_history: 대화 기록
            
        Returns:
            처리 결과
        """
        # user_id를 정수로 변환
        user_id_int = int(user_id) if user_id.isdigit() else self.user_id
        
        # process_message 메서드 호출
        result = self.process_message(message)
        
        # 결과에 type 필드 추가
        if "status" in result and result["status"] == "success":
            result["type"] = "food"
        
        return result
    
    def process_message(self, message: str) -> Dict[str, Any]:
        """
        사용자 메시지를 처리합니다.
        
        Args:
            message: 사용자 메시지
            
        Returns:
            처리 결과
        """
        try:
            # 의도 분석 - 단순화된 방식으로 진행
            intent_category = None
            
            # 키워드 기반 의도 분석 (간단한 방식으로 변경)
            msg_lower = message.lower()
            if any(word in msg_lower for word in ["먹었", "식사", "아침", "점심", "저녁", "야식"]):
                intent_category = "meal_input"
            elif any(word in msg_lower for word in ["추천", "식단", "메뉴", "음식", "뭐 먹"]):
                intent_category = "meal_recommendation"
            elif any(word in msg_lower for word in ["영양", "분석", "칼로리", "단백질", "탄수화물"]):
                intent_category = "nutrient_analysis"
            elif any(word in msg_lower for word in ["검색", "찾아", "뭐야", "정보"]):
                intent_category = "food_search"
            elif any(word in msg_lower for word in ["bmr", "tdee", "기초대사량", "대사량", "칼로리 계산"]):
                intent_category = "nutrition_calculation"
            
            print(f"분석된 의도: {intent_category}")
            
            # 워크플로우 실행 - 동기식 실행 및 카테고리 직접 전달
            try:
                # 미리 분석한 카테고리 워크플로우에 전달
                result = run_food_workflow(message, self.user_id, intent_category)
                
                # 출력 값이 없는 경우 기본 응답 생성
                if "output" not in result or not result["output"]:
                    result["output"] = "처리가 완료되었습니다. 추가 정보가 필요하시면 말씀해주세요."
                
                # 응답 생성
                response = {
                    "status": "success",
                    "output": result.get("output"),
                    "intent": intent_category or "unknown",
                    "type": "food"
                }
                
                # 오류 처리
                if result.get("error"):
                    response["status"] = "error"
                    response["error"] = result["error"]
                
                return response
            except Exception as workflow_error:
                print(f"워크플로우 실행 오류: {workflow_error}")
                return {
                    "status": "error",
                    "error": f"워크플로우 실행 오류: {str(workflow_error)}",
                    "output": "죄송합니다. 워크플로우 실행 중 오류가 발생했습니다.",
                    "type": "food"
                }
            
        except Exception as e:
            # 오류 로깅
            print(f"FoodAgent 처리 오류: {str(e)}")
            
            # 오류 응답 반환
            return {
                "status": "error",
                "error": f"FoodAgent 처리 오류: {str(e)}",
                "output": "죄송합니다. 처리 중 오류가 발생했습니다.",
                "type": "food"
            }
    
    def run(self, user_input: str) -> Dict[str, Any]:
        """
        에이전트를 실행합니다.
        
        Args:
            user_input: 사용자 입력
            
        Returns:
            처리 결과
        """
        try:
            # 의도 분석
            intent_category = None
            
            # 키워드 기반 의도 분석
            msg_lower = user_input.lower()
            if any(word in msg_lower for word in ["먹었", "식사", "아침", "점심", "저녁", "야식"]):
                intent_category = "meal_input"
            elif any(word in msg_lower for word in ["추천", "식단", "메뉴", "음식", "뭐 먹"]):
                intent_category = "meal_recommendation"
            elif any(word in msg_lower for word in ["영양", "분석", "칼로리", "단백질", "탄수화물"]):
                intent_category = "nutrient_analysis"
            elif any(word in msg_lower for word in ["검색", "찾아", "뭐야", "정보"]):
                intent_category = "food_search"
            elif any(word in msg_lower for word in ["bmr", "tdee", "기초대사량", "대사량", "칼로리 계산"]):
                intent_category = "nutrition_calculation"
            
            # 워크플로우 실행
            result = run_food_workflow(user_input, self.user_id, intent_category)
            
            # 결과 반환
            return result
            
        except Exception as e:
            print(f"에이전트 실행 오류: {str(e)}")
            return {
                "error": str(e),
                "output": f"에이전트 실행 중 오류가 발생했습니다: {str(e)}"
            }
    
 

 