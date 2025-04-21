"""
식사 에이전트 모듈
"""

from typing import Dict, Any, List
from langchain_core.messages import HumanMessage, AIMessage
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser
from langchain_core.runnables import RunnablePassthrough
from langchain_core.tools import tool

from .workflow import run_food_workflow
from .models.food_agent_state import FoodAgentState

class FoodAgent:
    """식사 관련 에이전트"""
    
    def __init__(self):
        """초기화"""
        self.state = FoodAgentState()
        
    async def process(self, message: str, user_id: int = 1) -> Dict[str, Any]:
        """
        사용자 메시지를 처리합니다.
        
        Args:
            message: 사용자 메시지
            user_id: 사용자 ID
            
        Returns:
            처리 결과
        """
        try:
            # 메시지 분석
            category = await self._analyze_message(message)
            
            # 초기 상태 생성
            initial_state = {
                "messages": [],
                "category": category,
                "user_id": user_id,
                "current_step": "start"
            }
            
            # 워크플로우 실행
            result = await run_food_workflow(initial_state)
            
            return {
                "type": "food",
                "response": result.get("response", "죄송합니다. 처리 중 오류가 발생했습니다."),
                "error": None
            }
            
        except Exception as e:
            return {
                "type": "food",
                "response": "죄송합니다. 처리 중 오류가 발생했습니다.",
                "error": str(e)
            }
            
    async def _analyze_message(self, message: str) -> str:
        """
        메시지를 분석하여 카테고리를 결정합니다.
        
        Args:
            message: 사용자 메시지
            
        Returns:
            카테고리
        """
        # 키워드 기반 카테고리 분류
        keywords = {
            "meal_input": ["식사", "먹었어", "식사했어", "밥", "아침", "점심", "저녁"],
            "meal_recommendation": ["추천", "추천해", "추천해줘", "뭐 먹을까"],
            "nutrition_query": ["영양", "칼로리", "단백질", "탄수화물", "지방"],
            "meal_planning": ["계획", "일정", "스케줄"],
            "nutrition_analysis": ["분석", "통계", "기록"]
        }
        
        message = message.lower()
        for category, words in keywords.items():
            if any(word in message for word in words):
                return category
                
        return "meal_input"  # 기본 카테고리 