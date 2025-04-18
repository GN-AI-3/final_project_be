"""
식단 관리 에이전트의 워크플로우 정의
"""
from typing import Dict, Any, TypedDict, Annotated, Sequence, List, Optional, cast
from langchain_core.messages import BaseMessage, HumanMessage, AIMessage
from langchain_openai import ChatOpenAI
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser
from langchain_core.runnables import RunnablePassthrough
from langchain_core.tools import tool
from langgraph.graph import StateGraph, END
from datetime import datetime, date, timedelta
import json

from .models.state_models import FoodAgentState
from .nodes import (
    create_routing_node,
    create_meal_input_node,
    create_balanced_meal_node,
    create_goal_conversion_node,
    create_nutrient_analysis_node,
    create_food_search_node,
    create_response_node,
    create_nutrition_calculation_node
)
from .nodes.food_nodes import (
    categorize_input,
    process_meal_input,
    process_meal_recommendation,
    process_nutrient_query,
    process_meal_planning,
    process_nutrition_analysis
)

def route_by_category(state: Dict[str, Any]) -> str:
    """
    상태의 카테고리에 따라 다음 노드를 결정합니다.
    
    Args:
        state: 현재 상태
        
    Returns:
        다음 노드의 이름
    """
    # 오류가 발생한 경우
    if state.get("error"):
        return "response"
    
    # 카테고리에 따라 다음 노드 결정
    category = state.get("category")
    if not category:
        return "response"
    
    if category == "meal_input":
        return "meal_input"
    elif category == "meal_recommendation":
        return "goal_conversion"
    elif category == "nutrient_analysis":
        return "nutrient_analysis"
    elif category == "food_search":
        return "food_search"
    elif category == "nutrition_calculation":
        return "nutrition_calculation"
    elif category == "meal_planning":
        return "meal_planning"
    else:
        return "response"

def create_food_workflow() -> StateGraph:
    """
    식단 관리 에이전트의 워크플로우를 생성합니다.
    
    Returns:
        생성된 워크플로우
    """
    # 워크플로우 생성
    workflow = StateGraph(FoodAgentState)
    
    # 노드 추가
    workflow.add_node("routing", create_routing_node())
    workflow.add_node("meal_input", create_meal_input_node())
    workflow.add_node("goal_conversion", create_goal_conversion_node())
    workflow.add_node("balanced_meal", create_balanced_meal_node())
    workflow.add_node("nutrient_analysis", create_nutrient_analysis_node())
    workflow.add_node("food_search", create_food_search_node())
    workflow.add_node("response", create_response_node())
    workflow.add_node("nutrition_calculation", create_nutrition_calculation_node())
    
    # 엣지 설정
    workflow.set_entry_point("routing")
    workflow.add_conditional_edges(
        "routing",
        route_by_category,
        {
            "meal_input": "meal_input",
            "goal_conversion": "goal_conversion",
            "nutrient_analysis": "nutrient_analysis",
            "food_search": "food_search",
            "nutrition_calculation": "nutrition_calculation",
            "meal_planning": "meal_planning",
            "response": "response"
        }
    )
    
    # 식단 추천 워크플로우: 목표 분석 -> 균형 식단 추천
    workflow.add_edge("goal_conversion", "balanced_meal")
    
    # 모든 노드는 결과적으로 응답 노드로 연결
    workflow.add_edge("meal_input", "response")
    workflow.add_edge("balanced_meal", "response")
    workflow.add_edge("nutrient_analysis", "response")
    workflow.add_edge("food_search", "response")
    workflow.add_edge("nutrition_calculation", "response")
    
    # 워크플로우 컴파일
    return workflow.compile()

async def run_food_workflow(initial_state: Dict[str, Any]) -> Dict[str, Any]:
    """
    식단 관리 워크플로우를 실행합니다.
    
    Args:
        initial_state: 초기 상태
        
    Returns:
        처리 결과
    """
    try:
        state = initial_state.copy()
        category = state.get("category", "")
        
        if category == "meal_input":
            return await process_meal_input(state)
        elif category == "meal_recommendation":
            return await process_meal_recommendation(state)
        elif category == "nutrient_query":
            return await process_nutrient_query(state)
        elif category == "meal_planning":
            return await process_meal_planning(state)
        else:
            return {
                "error": f"지원하지 않는 카테고리입니다: {category}",
                "category": category
            }
            
    except Exception as e:
        return {
            "error": f"워크플로우 실행 중 오류가 발생했습니다: {str(e)}",
            "category": initial_state.get("category", "")
        }

async def validate_state(state: Dict[str, Any]) -> bool:
    """
    상태 객체의 유효성을 검사합니다.
    
    Args:
        state: 상태 객체
        
    Returns:
        유효성 검사 결과
    """
    try:
        # 필수 필드 검사
        required_fields = ["category"]
        for field in required_fields:
            if field not in state:
                print(f"필수 필드가 없습니다: {field}")
                return False
        
        # 카테고리별 필수 필드 검사
        category = state.get("category", "").lower()
        
        if category == "meal_input":
            required_fields = ["meal_type", "food_items"]
            
        elif category == "meal_recommendation":
            required_fields = ["meal_type"]
            
        elif category == "nutrient_query":
            required_fields = ["food_name"]
            
        elif category == "nutrition_analysis":
            required_fields = ["user_id"]
            
        else:
            print(f"지원하지 않는 카테고리입니다: {category}")
            return False
        
        for field in required_fields:
            if field not in state:
                print(f"필수 필드가 없습니다: {field}")
                return False
        
        return True
        
    except Exception as e:
        print(f"상태 검증 오류: {str(e)}")
        return False

async def create_initial_state(
    category: str,
    user_id: int = 1,
    **kwargs
) -> Dict[str, Any]:
    """
    초기 상태 객체를 생성합니다.
    
    Args:
        category: 처리 카테고리
        user_id: 사용자 ID
        **kwargs: 추가 매개변수
        
    Returns:
        초기 상태 객체
    """
    try:
        # 기본 상태 생성
        state = {
            "category": category,
            "user_id": user_id,
            "timestamp": datetime.now().isoformat()
        }
        
        # 추가 매개변수 병합
        state.update(kwargs)
        
        # 상태 유효성 검사
        if not await validate_state(state):
            raise ValueError("유효하지 않은 상태 객체입니다.")
        
        return state
        
    except Exception as e:
        print(f"초기 상태 생성 오류: {str(e)}")
        raise
