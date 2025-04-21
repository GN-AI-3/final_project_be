"""
식단 관리 도구 모듈
"""
import os
import sys
from typing import Dict, Any, List, Optional
from datetime import datetime, date, timedelta
from langchain.tools import tool
import json
import requests
import psycopg2
from psycopg2.extras import RealDictCursor
from dotenv import load_dotenv
import random

from ..models.db_models import (
    User, 
    Food, 
    Meal,
    UserPreference
)
from .db_utils import execute_query

# 환경 변수 로드
load_dotenv()

# 데이터베이스 설정
DB_CONFIG = {
    "dbname": os.getenv("DB_NAME"),
    "user": os.getenv("DB_USER"),
    "password": os.getenv("DB_PASSWORD"),
    "host": os.getenv("DB_HOST"),
    "port": os.getenv("DB_PORT")
}

# 연결 풀
connection_pool = None

def get_connection_pool():
    """데이터베이스 연결 풀을 반환합니다."""
    global connection_pool
    if connection_pool is None:
        try:
            connection_pool = psycopg2.pool.SimpleConnectionPool(
                minconn=1,
                maxconn=10,
                **DB_CONFIG
            )
        except Exception as e:
            print(f"데이터베이스 연결 풀 생성 오류: {str(e)}")
            return None
    return connection_pool

def execute_query(query: str, params: Dict[str, Any] = None) -> List[Dict[str, Any]]:
    """쿼리를 실행하고 결과를 반환합니다."""
    pool = get_connection_pool()
    if pool is None:
        return []
    
    conn = None
    try:
        conn = pool.getconn()
        with conn.cursor(cursor_factory=RealDictCursor) as cur:
            cur.execute(query, params)
            if cur.description:
                return cur.fetchall()
            return []
    except Exception as e:
        print(f"쿼리 실행 오류: {str(e)}")
        return []
    finally:
        if conn:
            pool.putconn(conn)

class FoodTool:
    """식단 관리 도구 클래스"""

    def get_user_info(self, user_id: int) -> Dict[str, Any]:
        """사용자 정보 조회
        
        Args:
            user_id: 사용자 ID
            
        Returns:
            사용자 정보 딕셔너리
        """
        try:
            # 실제로는 DB에서 조회
            user_info = {
                "user_id": user_id,
                "name": "사용자",
                "gender": "남성",
                "age": 30,
                "height": 175,
                "weight": 70,
                "activity_level": "보통",
                "goal": "체중 유지"
            }
            return user_info
        except Exception as e:
            print(f"사용자 정보 조회 오류: {str(e)}")
            return {"error": "사용자 정보 조회 중 오류가 발생했습니다."}
            
    def get_user_preferences(self, user_id: int) -> Dict[str, Any]:
        """사용자 선호도 조회
        
        Args:
            user_id: 사용자 ID
            
        Returns:
            사용자 선호도 딕셔너리
        """
        try:
            # 실제로는 DB에서 조회
            preferences = {
                "liked_foods": ["닭가슴살", "연어", "브로콜리", "고구마", "현미밥"],
                "disliked_foods": ["돼지고기", "양파"],
                "allergies": ["땅콩", "새우"],
                "dietary_restrictions": ["저염"],
                "favorite_cuisine": ["한식", "일식"]
            }
            return {"preferences": preferences}
        except Exception as e:
            print(f"사용자 선호도 조회 오류: {str(e)}")
            return {"error": "사용자 선호도 조회 중 오류가 발생했습니다."}
    
    @tool
    async def save_meal_record(self, user_id: int, meal_type: str, foods: List[Dict[str, Any]]) -> str:
        """
        식사 기록을 저장합니다.
        
        Args:
            user_id: 사용자 ID
            meal_type: 식사 유형
            foods: 음식 목록
            
        Returns:
            저장 결과
        """
        try:
            # 식사 기록 저장
            query = """
            INSERT INTO meal_records (user_id, meal_type, foods)
            VALUES (%(user_id)s, %(meal_type)s, %(foods)s)
            RETURNING id
            """
            
            result = await execute_query(
                query,
                {
                    "user_id": user_id,
                    "meal_type": meal_type,
                    "foods": foods
                }
            )
            
            if result:
                return f"식사 기록이 저장되었습니다. (ID: {result[0]['id']})"
            else:
                return "식사 기록 저장에 실패했습니다."
                
        except Exception as e:
            return f"식사 기록 저장 중 오류가 발생했습니다: {str(e)}"
    
    def get_today_meals(self, user_id: int) -> Dict[str, Any]:
        """오늘의 식사 기록 조회
        
        Args:
            user_id: 사용자 ID
            
        Returns:
            오늘의 식사 기록
        """
        try:
            # 실제로는 DB에서 조회
            today = datetime.now().strftime("%Y-%m-%d")
            
            # 더미 데이터 생성
            breakfast = {
                "meal_type": "아침",
                "meal_time": f"{today} 07:30:00",
                "foods": [
                    {"name": "현미밥", "amount": "1공기", "calories": 150},
                    {"name": "계란후라이", "amount": "2개", "calories": 180},
                    {"name": "김치", "amount": "1접시", "calories": 50}
                ]
            }
            
            lunch = {
                "meal_type": "점심",
                "meal_time": f"{today} 12:30:00",
                "foods": [
                    {"name": "닭가슴살 샐러드", "amount": "1접시", "calories": 300},
                    {"name": "통밀빵", "amount": "2조각", "calories": 160}
                ]
            }
            
            # 현재 시간이 저녁 이후라면 저녁 식사 추가
            current_hour = datetime.now().hour
            meals = [breakfast, lunch]
            
            if current_hour >= 18:
                dinner = {
                    "meal_type": "저녁",
                    "meal_time": f"{today} 18:30:00",
                    "foods": [
                        {"name": "연어스테이크", "amount": "1조각", "calories": 250},
                        {"name": "샐러드", "amount": "1접시", "calories": 100},
                        {"name": "귀리밥", "amount": "1/2공기", "calories": 100}
                    ]
                }
                meals.append(dinner)
            
            return {"today_meals": meals}
        except Exception as e:
            print(f"오늘의 식사 조회 오류: {str(e)}")
            return {"error": "오늘의 식사 조회 중 오류가 발생했습니다."}
    
    def get_weekly_meals(self, user_id: int) -> Dict[str, Any]:
        """최근 일주일 식사 기록 조회
        
        Args:
            user_id: 사용자 ID
            
        Returns:
            최근 일주일 식사 기록
        """
        try:
            # 실제로는 DB에서 조회
            today = datetime.now()
            weekly_meals = []
            
            # 7일간의 더미 데이터 생성
            for i in range(7):
                day = today - timedelta(days=i)
                day_str = day.strftime("%Y-%m-%d")
                
                # 하루 식사 데이터
                daily_meals = {
                    "date": day_str,
                    "meals": [
                        {
                            "meal_type": "아침",
                            "foods": [
                                {"name": "시리얼", "amount": "1공기", "calories": 200},
                                {"name": "우유", "amount": "200ml", "calories": 120}
                            ]
                        },
                        {
                            "meal_type": "점심",
                            "foods": [
                                {"name": "비빔밥", "amount": "1공기", "calories": 500},
                                {"name": "된장국", "amount": "1그릇", "calories": 100}
                            ]
                        },
                        {
                            "meal_type": "저녁",
                            "foods": [
                                {"name": "닭가슴살", "amount": "100g", "calories": 165},
                                {"name": "샐러드", "amount": "1접시", "calories": 100},
                                {"name": "현미밥", "amount": "1/2공기", "calories": 100}
                            ]
                        }
                    ]
                }
                
                weekly_meals.append(daily_meals)
            
            return {"weekly_meals": weekly_meals}
        except Exception as e:
            print(f"주간 식사 조회 오류: {str(e)}")
            return {"error": "주간 식사 조회 중 오류가 발생했습니다."}
    
    @tool
    async def recommend_balanced_meal(self, user_id: int, meal_type: str) -> str:
        """
        균형 잡힌 식사를 추천합니다.
        
        Args:
            user_id: 사용자 ID
            meal_type: 식사 유형
            
        Returns:
            추천 결과
        """
        try:
            # 사용자 선호도 조회
            query = """
            SELECT preferences
            FROM user_preferences
            WHERE user_id = %(user_id)s
            """
            
            preferences = await execute_query(query, {"user_id": user_id})
            
            if not preferences:
                return "사용자 선호도 정보가 없습니다."
                
            # 식사 추천
            query = """
            SELECT *
            FROM foods
            WHERE meal_type = %(meal_type)s
            AND calories BETWEEN 300 AND 800
            ORDER BY RANDOM()
            LIMIT 3
            """
            
            recommendations = await execute_query(
                query,
                {"meal_type": meal_type}
            )
            
            if not recommendations:
                return "추천할 수 있는 식사가 없습니다."
                
            return f"추천 식사: {[food['name'] for food in recommendations]}"
            
        except Exception as e:
            return f"식사 추천 중 오류가 발생했습니다: {str(e)}"
            
    @tool
    async def get_food_nutrition(self, food_name: str) -> str:
        """
        음식의 영양 정보를 조회합니다.
        
        Args:
            food_name: 음식 이름
            
        Returns:
            영양 정보
        """
        try:
            # 영양 정보 조회
            query = """
            SELECT *
            FROM foods
            WHERE name = %(food_name)s
            """
            
            result = await execute_query(query, {"food_name": food_name})
            
            if not result:
                return f"{food_name}의 영양 정보를 찾을 수 없습니다."
                
            food = result[0]
            return f"""
            {food['name']}의 영양 정보:
            - 칼로리: {food['calories']}kcal
            - 단백질: {food['protein']}g
            - 탄수화물: {food['carbs']}g
            - 지방: {food['fat']}g
            """
            
        except Exception as e:
            return f"영양 정보 조회 중 오류가 발생했습니다: {str(e)}"

    def search_food(self, query: str) -> Dict[str, Any]:
        """음식 검색
        
        Args:
            query: 검색어
            
        Returns:
            검색 결과
        """
        try:
            # 실제로는 DB에서 검색
            # 기본 음식 목록
            food_list = [
                "밥", "현미밥", "김치", "된장국", "닭가슴살", "연어", 
                "계란", "두부", "시리얼", "우유", "사과", "바나나", 
                "오렌지", "브로콜리", "당근", "고구마"
            ]
            
            # 검색어와 일치하는 음식 찾기
            results = []
            for food in food_list:
                if query.lower() in food.lower():
                    # 해당 음식의 영양 정보 조회
                    food_info = self.get_food_nutrition(food)
                    results.append({
                        "name": food,
                        "nutrition": food_info.get("nutrition", {}),
                        "serving_size": food_info.get("serving_size", "1인분")
                    })
            
            if not results:
                return {
                    "query": query,
                    "results": [],
                    "message": "검색 결과가 없습니다."
                }
            
            return {
                "query": query,
                "results": results,
                "count": len(results)
            }
        except Exception as e:
            print(f"음식 검색 오류: {str(e)}")
            return {"error": "음식 검색 중 오류가 발생했습니다."}

    @tool
    async def process_meal_planning(self, user_id: int, days: int = 7) -> str:
        """
        식사 계획을 생성합니다.
        
        Args:
            user_id: 사용자 ID
            days: 계획 일수
            
        Returns:
            식사 계획
        """
        try:
            # 사용자 선호도 조회
            preferences = await self.get_user_preferences(user_id)
            
            # 사용자 정보 조회
            user_info = await self.get_user_info(user_id)
            
            # 식사 계획 생성
            meal_plan = {
                "user_id": user_id,
                "days": days,
                "plan": []
            }
            
            # 더미 데이터로 계획 생성
            for day in range(days):
                daily_plan = {
                    "date": (datetime.now() + timedelta(days=day)).strftime("%Y-%m-%d"),
                    "meals": {
                        "breakfast": ["현미밥", "계란후라이", "김치"],
                        "lunch": ["닭가슴살 샐러드", "통밀빵"],
                        "dinner": ["연어스테이크", "샐러드", "귀리밥"]
                    }
                }
                meal_plan["plan"].append(daily_plan)
            
            return json.dumps(meal_plan, ensure_ascii=False)
            
        except Exception as e:
            return f"식사 계획 생성 중 오류가 발생했습니다: {str(e)}"

class NutritionTool:
    """영양 분석 도구 클래스"""
    
    def calculate_bmr(self, gender: str, age: int, weight: float, height: float) -> Dict[str, Any]:
        """기초 대사량 계산
        
        Args:
            gender: 성별 (남성, 여성)
            age: 나이
            weight: 체중 (kg)
            height: 키 (cm)
            
        Returns:
            BMR 계산 결과
        """
        try:
            # 해리스-베네딕트 공식
            if gender == "남성":
                bmr = 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age)
            else:  # 여성
                bmr = 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age)
                
            return {
                "bmr": round(bmr, 2),
                "message": f"기초 대사량(BMR)은 하루 약 {int(bmr)}kcal입니다."
            }
        except Exception as e:
            print(f"BMR 계산 오류: {str(e)}")
            return {"error": "BMR 계산 중 오류가 발생했습니다."}
    
    def calculate_tdee(self, bmr: float, activity_level: str) -> Dict[str, Any]:
        """총 에너지 소비량 계산
        
        Args:
            bmr: 기초 대사량
            activity_level: 활동 수준
            
        Returns:
            TDEE 계산 결과
        """
        try:
            # 활동 레벨별 계수
            activity_factors = {
                "거의 없음": 1.2,
                "가벼운 활동": 1.375,
                "보통 활동": 1.55,
                "활발한 활동": 1.725,
                "매우 활발한 활동": 1.9
            }
            
            # 기본값 설정
            factor = activity_factors.get(activity_level, 1.55)
            
            tdee = bmr * factor
            
            return {
                "tdee": round(tdee, 2),
                "message": f"활동 수준 '{activity_level}'을 고려한 일일 총 에너지 소비량(TDEE)은 약 {int(tdee)}kcal입니다."
            }
        except Exception as e:
            print(f"TDEE 계산 오류: {str(e)}")
            return {"error": "TDEE 계산 중 오류가 발생했습니다."}
    
    def recommend_calories(self, tdee: float, goal: str) -> Dict[str, Any]:
        """목표에 따른 칼로리 추천
        
        Args:
            tdee: 총 에너지 소비량
            goal: 목표 (체중 감소, 체중 유지, 체중 증가)
            
        Returns:
            추천 칼로리
        """
        try:
            if goal == "체중 감소":
                calories = tdee * 0.8  # 20% 감소
                message = "체중 감소를 위해 칼로리 섭취를 20% 줄이는 것이 좋습니다."
            elif goal == "체중 증가":
                calories = tdee * 1.15  # 15% 증가
                message = "체중 증가를 위해 칼로리 섭취를 15% 늘리는 것이 좋습니다."
            else:  # 체중 유지
                calories = tdee
                message = "체중 유지를 위해 현재 TDEE와 비슷한 칼로리를 섭취하는 것이 좋습니다."
                
            return {
                "recommended_calories": round(calories, 2),
                "message": message
            }
        except Exception as e:
            print(f"칼로리 추천 오류: {str(e)}")
            return {"error": "칼로리 추천 중 오류가 발생했습니다."}
    
    def analyze_nutrient_balance(self, foods: List[Dict[str, Any]]) -> Dict[str, Any]:
        """영양소 균형 분석
        
        Args:
            foods: 음식 목록
            
        Returns:
            영양소 분석 결과
        """
        try:
            # 영양소 합계 계산
            total_calories = 0
            total_protein = 0
            total_carbs = 0
            total_fats = 0
            
            for food in foods:
                nutrition = food.get("nutrition", {})
                total_calories += nutrition.get("calories", 0)
                total_protein += nutrition.get("protein", 0)
                total_carbs += nutrition.get("carbs", 0)
                total_fats += nutrition.get("fats", 0)
            
            # 영양소 비율 계산
            if total_calories > 0:
                protein_ratio = (total_protein * 4 / total_calories) * 100
                carbs_ratio = (total_carbs * 4 / total_calories) * 100
                fats_ratio = (total_fats * 9 / total_calories) * 100
            else:
                protein_ratio = 0
                carbs_ratio = 0
                fats_ratio = 0
            
            # 균형 평가
            balance_evaluation = "균형 잡힘"
            recommendations = []
            
            if protein_ratio < 10:
                balance_evaluation = "단백질 부족"
                recommendations.append("단백질 섭취를 늘리세요. 닭가슴살, 계란, 두부 등을 추가하세요.")
            elif protein_ratio > 35:
                balance_evaluation = "단백질 과다"
                recommendations.append("단백질 섭취가 높습니다. 탄수화물과 건강한 지방을 늘리세요.")
                
            if carbs_ratio < 40:
                balance_evaluation = "탄수화물 부족"
                recommendations.append("탄수화물 섭취를 늘리세요. 현미, 오트밀, 고구마 등을 추가하세요.")
            elif carbs_ratio > 65:
                balance_evaluation = "탄수화물 과다"
                recommendations.append("탄수화물 섭취가 높습니다. 단백질과 건강한 지방을 늘리세요.")
                
            if fats_ratio < 20:
                recommendations.append("건강한 지방 섭취를 늘리세요. 아보카도, 견과류, 올리브 오일 등을 추가하세요.")
            elif fats_ratio > 35:
                recommendations.append("지방 섭취가 높습니다. 단백질과 복합 탄수화물을 늘리세요.")
                
            if not recommendations:
                recommendations.append("현재 영양소 비율이 균형적입니다. 이 식단을 유지하세요.")
            
            return {
                "total_nutrition": {
                    "calories": total_calories,
                    "protein": {
                        "grams": total_protein,
                        "calories": total_protein * 4,
                        "ratio": round(protein_ratio, 1)
                    },
                    "carbs": {
                        "grams": total_carbs,
                        "calories": total_carbs * 4,
                        "ratio": round(carbs_ratio, 1)
                    },
                    "fats": {
                        "grams": total_fats,
                        "calories": total_fats * 9,
                        "ratio": round(fats_ratio, 1)
                    }
                },
                "balance_evaluation": balance_evaluation,
                "recommendations": recommendations
            }
        except Exception as e:
            print(f"영양소 분석 오류: {str(e)}")
            return {"error": "영양소 분석 중 오류가 발생했습니다."} 