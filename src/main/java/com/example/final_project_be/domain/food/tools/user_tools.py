"""
사용자 정보 관련 도구 정의
"""

from typing import Dict, Any, List, Optional
from langchain.tools import Tool
from .db_utils import execute_query

class UserTool:
    """사용자 정보 관련 도구 클래스"""
    
    @staticmethod
    def get_user_info(user_id: int) -> Dict[str, Any]:
        """
        사용자 정보를 조회합니다.
        
        Args:
            user_id: 사용자 ID
            
        Returns:
            사용자 정보
        """
        query = """
        SELECT * 
        FROM member join inbody on member.id = inbody.member_id
        WHERE member.id = %(user_id)s
        """
        result = execute_query(query, {"user_id": user_id})
        
        if not result:
            return {
                "gender": "미상",
                "age": 30,
                "height": 170,
                "weight": 65,
                "activity_level": "보통",
                "goal": "건강 유지"
            }
        
        user = result[0]
        return {
            "gender": user.get("gender", "미상"),
            "age": user.get("age", 30),
            "height": user.get("height", 170),
            "weight": user.get("weight", 65),
            "activity_level": user.get("activity_level", "보통"),
            "goal": user.get("goal", "건강 유지")
        }
    
    @staticmethod
    def get_user_preferences(user_id: int) -> Dict[str, Any]:
        """
        사용자의 식사 선호도를 조회합니다.
        
        Args:
            user_id: 사용자 ID
            
        Returns:
            사용자 선호도 정보
        """
        query = """
        SELECT * 
        FROM user_preferences 
        WHERE user_id = %(user_id)s
        """
        result = execute_query(query, {"user_id": user_id})
        
        if not result:
            return {
                "allergies": "없음",
                "dietary_preference": "없음",
                "meal_pattern": "아침, 점심, 저녁 3식",
                "meal_times": "아침 7시, 점심 12시, 저녁 6시",
                "food_preferences": "한식 선호",
                "special_requirements": "없음"
            }
        
        pref = result[0]
        return {
            "allergies": pref.get("allergies", "없음"),
            "dietary_preference": pref.get("dietary_preference", "없음"),
            "meal_pattern": pref.get("meal_pattern", "아침, 점심, 저녁 3식"),
            "meal_times": pref.get("meal_times", "아침 7시, 점심 12시, 저녁 6시"),
            "food_preferences": pref.get("food_preferences", "한식 선호"),
            "special_requirements": pref.get("special_requirements", "없음")
        }
    
    @staticmethod
    def update_user_info(user_info: Dict[str, Any]) -> Dict[str, Any]:
        """
        사용자 정보를 업데이트합니다.
        
        Args:
            user_info: 업데이트할 사용자 정보
            
        Returns:
            업데이트 결과
        """
        query = """
        UPDATE users 
        SET gender = %(gender)s, 
            age = %(age)s, 
            height = %(height)s, 
            weight = %(weight)s, 
            activity_level = %(activity_level)s, 
            goal = %(goal)s
        WHERE id = %(user_id)s
        """
        return execute_query(query, user_info)
    
    # 도구 정의
    get_user_info = Tool(
        name="get_user_info",
        func=get_user_info,
        description="사용자 정보를 조회합니다."
    )
    
    get_user_preferences = Tool(
        name="get_user_preferences",
        func=get_user_preferences,
        description="사용자의 식사 선호도를 조회합니다."
    )
    
    update_user_info = Tool(
        name="update_user_info",
        func=update_user_info,
        description="사용자 정보를 업데이트합니다."
    ) 