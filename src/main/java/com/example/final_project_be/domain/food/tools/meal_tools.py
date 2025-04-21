"""
식사 관련 도구 정의
"""

from typing import Dict, Any, List, Optional
from langchain.tools import tool
import json
from datetime import datetime, date, timedelta
import random

from .db_utils import execute_query
from .search_tools import SearchTool

class MealTool:
    """식사 관련 도구 클래스"""
    
    @staticmethod
    async def save_meal_record(user_id: int, meal_type: str, food_items: List[Dict[str, Any]]) -> Dict[str, Any]:
        """
        식사 기록을 저장합니다.
        
        Args:
            user_id: 사용자 ID
            meal_type: 식사 유형 (아침, 점심, 저녁, 간식)
            food_items: 음식 항목 목록
            
        Returns:
            저장 결과
        """
        try:
            # 음식 항목의 영양 정보 계산
            total_calories = 0
            total_protein = 0
            total_carbs = 0
            total_fat = 0
            
            for food in food_items:
                food_name = food.get("name", "")
                portion = food.get("portion", 1.0)
                
                # 음식 영양 정보 조회
                nutrition_info = await SearchTool.search_food(food_name)
                
                if nutrition_info:
                    # 영양 정보 계산
                    total_calories += nutrition_info.get("calories", 0) * portion
                    total_protein += nutrition_info.get("protein", 0) * portion
                    total_carbs += nutrition_info.get("carbs", 0) * portion
                    total_fat += nutrition_info.get("fat", 0) * portion
            
            # 식사 기록 저장
            query = """
            INSERT INTO meal_records (
                user_id, food_name, portion, unit, meal_date, meal_time, 
                meal_type, calories, protein, carbs, fat, created_at
            )
            VALUES (
                %(user_id)s, %(food_name)s, %(portion)s, %(unit)s, 
                CURRENT_DATE, CURRENT_TIME, %(meal_type)s, %(calories)s, 
                %(protein)s, %(carbs)s, %(fat)s, CURRENT_TIMESTAMP
            )
            RETURNING id
            """
            
            # 각 음식 항목별로 저장
            meal_ids = []
            for food in food_items:
                food_name = food.get("name", "")
                portion = food.get("portion", 1.0)
                unit = food.get("unit", "g")
                
                # 음식 영양 정보 조회
                nutrition_info = await SearchTool.search_food(food_name)
                
                if nutrition_info:
                    # 영양 정보 계산
                    calories = nutrition_info.get("calories", 0) * portion
                    protein = nutrition_info.get("protein", 0) * portion
                    carbs = nutrition_info.get("carbs", 0) * portion
                    fat = nutrition_info.get("fat", 0) * portion
                    
                    # 데이터베이스에 저장
                    result = await execute_query(
                        query, 
                        {
                            "user_id": user_id,
                            "food_name": food_name,
                            "portion": portion,
                            "unit": unit,
                            "meal_type": meal_type,
                            "calories": calories,
                            "protein": protein,
                            "carbs": carbs,
                            "fat": fat
                        }
                    )
                    
                    if result:
                        meal_ids.append(result[0].get("id"))
            
            return {
                "success": True,
                "meal_ids": meal_ids,
                "total_nutrition": {
                    "calories": total_calories,
                    "protein": total_protein,
                    "carbs": total_carbs,
                    "fat": total_fat
                }
            }
            
        except Exception as e:
            print(f"식사 기록 저장 오류: {str(e)}")
            return {
                "success": False,
                "error": str(e)
            }
    
    @staticmethod
    async def get_diet_plan(diet_type: str, gender: str = "M", period: str = "daily") -> Dict[str, Any]:
        """
        식단 계획을 조회합니다.
        
        Args:
            diet_type: 식단 유형 (다이어트, 증량, 유지 등)
            gender: 성별 (M, F)
            period: 기간 (daily, weekly)
            
        Returns:
            식단 계획 정보
        """
        try:
            # 식단 계획 조회
            query = """
            SELECT * 
            FROM diet_plans 
            WHERE diet_type = %(diet_type)s
            AND user_gender = %(gender)s
            ORDER BY RANDOM()
            LIMIT 1
            """
            
            result = await execute_query(query, {"diet_type": diet_type, "gender": gender})
            
            if not result:
                # 식단 계획이 없는 경우 기본 식단 제공
                return {
                    "diet_type": diet_type,
                    "breakfast": "삶은 달걀 2개(100g), 오이 슬라이스 100g, 블랙커피",
                    "lunch": "닭가슴살 샐러드 200g(닭가슴살+양상추 150g+올리브유 10ml), 고구마 100g",
                    "dinner": "연어구이 150g, 브로콜리 찜 150g, 배추김치 50g",
                    "user_gender": gender
                }
            
            diet_plan = result[0]
            
            # 주간 식단인 경우 7일치 식단 생성
            if period == "weekly":
                weekly_plan = []
                for i in range(7):
                    day_plan = {
                        "day": i + 1,
                        "breakfast": diet_plan.get("breakfast", ""),
                        "lunch": diet_plan.get("lunch", ""),
                        "dinner": diet_plan.get("dinner", "")
                    }
                    weekly_plan.append(day_plan)
                
                return {
                    "diet_type": diet_type,
                    "user_gender": gender,
                    "period": "weekly",
                    "weekly_plan": weekly_plan
                }
            
            # 일일 식단인 경우
            return {
                "diet_type": diet_type,
                "user_gender": gender,
                "period": "daily",
                "breakfast": diet_plan.get("breakfast", ""),
                "lunch": diet_plan.get("lunch", ""),
                "dinner": diet_plan.get("dinner", "")
            }
            
        except Exception as e:
            print(f"식단 계획 조회 오류: {str(e)}")
            return {
                "error": str(e)
            }
    
    @staticmethod
    async def recommend_balanced_meal(user_id: int, meal_type: str, preferences: Optional[Dict[str, Any]] = None) -> Dict[str, Any]:
        """
        균형 잡힌 식사를 추천합니다.
        
        Args:
            user_id: 사용자 ID
            meal_type: 식사 유형 (아침, 점심, 저녁)
            preferences: 사용자 선호도
            
        Returns:
            추천 식사 정보
        """
        try:
            # 사용자 정보 조회
            user_query = """
            SELECT m.*, udi.*
            FROM member m
            LEFT JOIN user_diet_info udi ON m.member_id = udi.member_id
            WHERE m.member_id = %(user_id)s
            """
            
            user_result = await execute_query(user_query, {"user_id": user_id})
            
            if not user_result:
                return {
                    "error": "사용자 정보를 찾을 수 없습니다."
                }
            
            user_info = user_result[0]
            
            # 사용자 선호도 및 제한사항 확인
            allergies = user_info.get("allergies", "")
            dietary_preference = user_info.get("dietary_preference", "")
            food_preferences = user_info.get("food_preferences", "")
            
            # 식사 유형별 칼로리 범위 설정
            calorie_ranges = {
                "아침": (300, 500),
                "점심": (500, 700),
                "저녁": (400, 600)
            }
            
            min_calories, max_calories = calorie_ranges.get(meal_type, (400, 600))
            
            # 식사 추천 쿼리
            meal_query = """
            SELECT f.*, n.*
            FROM food_nutrition n
            JOIN foods f ON n.food_id = f.id
            WHERE n.calories BETWEEN %(min_calories)s AND %(max_calories)s
            """
            
            # 알레르기 및 선호도 필터링
            if allergies:
                meal_query += " AND f.allergens NOT LIKE %(allergies)s"
            
            if dietary_preference:
                meal_query += " AND f.category = %(dietary_preference)s"
            
            meal_query += " ORDER BY RANDOM() LIMIT 5"
            
            meal_result = await execute_query(
                meal_query, 
                {
                    "min_calories": min_calories,
                    "max_calories": max_calories,
                    "allergies": f"%{allergies}%",
                    "dietary_preference": dietary_preference
                }
            )
            
            if not meal_result:
                # 데이터베이스에서 결과가 없는 경우 웹 검색
                web_search_result = await SearchTool.web_search_food(f"{meal_type} meal {dietary_preference}")
                
                if web_search_result:
                    return {
                        "meal_type": meal_type,
                        "recommendations": web_search_result,
                        "source": "web_search"
                    }
                
                return {
                    "error": "추천할 수 있는 식사가 없습니다."
                }
            
            # 추천 결과 구성
            recommendations = []
            for food in meal_result:
                recommendations.append({
                    "name": food.get("name", ""),
                    "calories": food.get("calories", 0),
                    "protein": food.get("protein", 0),
                    "carbs": food.get("carbs", 0),
                    "fat": food.get("fat", 0),
                    "portion": "100g"
                })
            
            return {
                "meal_type": meal_type,
                "recommendations": recommendations,
                "source": "database"
            }
            
        except Exception as e:
            print(f"균형 잡힌 식사 추천 오류: {str(e)}")
            return {
                "error": str(e)
            }
    
    # 도구 정의
    @tool("save_meal_record")
    async def save_meal_record_tool(user_id: int, meal_type: str, food_items: List[Dict[str, Any]]) -> str:
        """
        식사 기록을 저장합니다.
        
        Args:
            user_id: 사용자 ID
            meal_type: 식사 유형 (아침, 점심, 저녁, 간식)
            food_items: 음식 항목 목록
        """
        result = await MealTool.save_meal_record(user_id, meal_type, food_items)
        return json.dumps(result, ensure_ascii=False)
    
    @tool("get_diet_plan")
    async def get_diet_plan_tool(diet_type: str, gender: str = "M", period: str = "daily") -> str:
        """
        식단 계획을 조회합니다.
        
        Args:
            diet_type: 식단 유형 (다이어트, 증량, 유지 등)
            gender: 성별 (M, F)
            period: 기간 (daily, weekly)
        """
        result = await MealTool.get_diet_plan(diet_type, gender, period)
        return json.dumps(result, ensure_ascii=False)
    
    @tool("recommend_balanced_meal")
    async def recommend_balanced_meal_tool(user_id: int, meal_type: str, preferences: Optional[Dict[str, Any]] = None) -> str:
        """
        균형 잡힌 식사를 추천합니다.
        
        Args:
            user_id: 사용자 ID
            meal_type: 식사 유형 (아침, 점심, 저녁)
            preferences: 사용자 선호도
        """
        result = await MealTool.recommend_balanced_meal(user_id, meal_type, preferences)
        return json.dumps(result, ensure_ascii=False) 