"""
영양 관리 도구 모듈
"""
import os
import sys
from datetime import datetime, timedelta
from typing import Dict, Any, List, Optional
import json
import psycopg2
from psycopg2.extras import RealDictCursor
from dotenv import load_dotenv
from langchain.tools import tool

from .db_utils import execute_query

# 환경 변수 로드
load_dotenv()

class NutritionTool:
    """영양 정보 계산과 관련된 도구를 제공하는 클래스"""
    
    @staticmethod
    async def calculate_bmr(gender: str, age: int, weight: float, height: float) -> float:
        """
        기초 대사량(BMR)을 계산합니다.
        
        Args:
            gender: 성별 ('남성' 또는 '여성')
            age: 나이
            weight: 체중(kg)
            height: 신장(cm)
            
        Returns:
            기초 대사량(kcal)
        """
        try:
            if gender.lower() in ['남성', '남자', 'male', 'm']:
                # 해리스-베네딕트 공식 (남성)
                bmr = 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age)
            else:
                # 해리스-베네딕트 공식 (여성)
                bmr = 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age)
            
            return round(bmr)
        except Exception as e:
            print(f"BMR 계산 오류: {str(e)}")
            return 0
    
    @staticmethod
    async def calculate_tdee(bmr: float, activity_level: str) -> float:
        """
        총 일일 에너지 소비량(TDEE)을 계산합니다.
        
        Args:
            bmr: 기초 대사량
            activity_level: 활동 수준
            
        Returns:
            총 일일 에너지 소비량(kcal)
        """
        try:
            activity_multipliers = {
                '매우 낮음': 1.2,  # 거의 활동 없음
                '낮음': 1.375,     # 가벼운 활동 (주 1-3회 운동)
                '보통': 1.55,      # 보통 활동 (주 3-5회 운동)
                '높음': 1.725,     # 활발한 활동 (주 6-7회 운동)
                '매우 높음': 1.9   # 매우 활발한 활동 (운동선수 수준)
            }
            
            # 활동 수준이 매핑에 없는 경우 '보통'으로 기본 처리
            multiplier = activity_multipliers.get(activity_level, 1.55)
            
            return round(bmr * multiplier)
        except Exception as e:
            print(f"TDEE 계산 오류: {str(e)}")
            return 0
    
    @staticmethod
    async def calculate_macros(tdee: float, goal: str) -> Dict[str, Any]:
        """
        목표에 따른 거시 영양소(매크로) 분배를 계산합니다.
        
        Args:
            tdee: 총 일일 에너지 소비량
            goal: 영양 목표 (다이어트, 증량, 유지 등)
            
        Returns:
            거시 영양소 분배 정보
        """
        try:
            # 목표에 따른 칼로리 조정
            adjusted_calories = tdee
            
            if '다이어트' in goal or '감량' in goal or '체중 감소' in goal:
                adjusted_calories = tdee * 0.8  # 20% 감소
            elif '증량' in goal or '벌크업' in goal or '체중 증가' in goal:
                adjusted_calories = tdee * 1.1  # 10% 증가
            
            # 기본 매크로 비율 (단백질:탄수화물:지방)
            protein_ratio = 0.3  # 30%
            carb_ratio = 0.4     # 40%
            fat_ratio = 0.3      # 30%
            
            # 목표에 따른 매크로 비율 조정
            if '다이어트' in goal or '감량' in goal:
                protein_ratio = 0.35  # 35%
                carb_ratio = 0.35     # 35%
                fat_ratio = 0.3       # 30%
            elif '증량' in goal or '벌크업' in goal:
                protein_ratio = 0.3   # 30%
                carb_ratio = 0.5      # 50%
                fat_ratio = 0.2       # 20%
            
            # 영양소별 칼로리 및 그램 계산
            protein_calories = adjusted_calories * protein_ratio
            carb_calories = adjusted_calories * carb_ratio
            fat_calories = adjusted_calories * fat_ratio
            
            protein_grams = round(protein_calories / 4)  # 단백질 1g = 4kcal
            carb_grams = round(carb_calories / 4)        # 탄수화물 1g = 4kcal
            fat_grams = round(fat_calories / 9)          # 지방 1g = 9kcal
            
            return {
                "protein": {
                    "ratio": protein_ratio,
                    "calories": round(protein_calories),
                    "grams": protein_grams
                },
                "carbs": {
                    "ratio": carb_ratio,
                    "calories": round(carb_calories),
                    "grams": carb_grams
                },
                "fats": {
                    "ratio": fat_ratio,
                    "calories": round(fat_calories),
                    "grams": fat_grams
                }
            }
        except Exception as e:
            print(f"매크로 계산 오류: {str(e)}")
            return {}
    
    @staticmethod
    async def calculate_nutrition_plan(gender: str, age: int, weight: float, height: float, 
                               activity_level: str, goal: str) -> Dict[str, Any]:
        """
        영양 계획을 계산합니다.
        
        Args:
            gender: 성별
            age: 나이
            weight: 체중(kg)
            height: 신장(cm)
            activity_level: 활동 수준
            goal: 영양 목표
            
        Returns:
            영양 계획 정보
        """
        try:
            # BMR 계산
            bmr = await NutritionTool.calculate_bmr(gender, age, weight, height)
            
            # TDEE 계산
            tdee = await NutritionTool.calculate_tdee(bmr, activity_level)
            
            # 목표에 따른 조정된 칼로리
            adjusted_calories = tdee
            if '다이어트' in goal or '감량' in goal or '체중 감소' in goal:
                adjusted_calories = round(tdee * 0.8)  # 20% 감소
            elif '증량' in goal or '벌크업' in goal or '체중 증가' in goal:
                adjusted_calories = round(tdee * 1.1)  # 10% 증가
            
            # 매크로 계산
            macros = await NutritionTool.calculate_macros(tdee, goal)
            
            return {
                "energy": {
                    "bmr": bmr,
                    "tdee": tdee,
                    "adjusted_calories": adjusted_calories
                },
                "macros": macros,
                "user_info": {
                    "gender": gender,
                    "age": age,
                    "weight": weight,
                    "height": height,
                    "activity_level": activity_level,
                    "goal": goal
                }
            }
        except Exception as e:
            print(f"영양 계획 계산 오류: {str(e)}")
            return {}
    
    @staticmethod
    async def get_weekly_meals(user_id: int) -> List[Dict[str, Any]]:
        """
        주간 식사 기록을 조회합니다.
        
        Args:
            user_id: 사용자 ID
            
        Returns:
            주간 식사 기록
        """
        query = """
        SELECT *
        FROM meal_records
        WHERE user_id = %(user_id)s
        AND meal_date >= CURRENT_DATE - INTERVAL '7 days'
        ORDER BY meal_date DESC
        """
        return await execute_query(query, {"user_id": user_id})
    
    @staticmethod
    async def analyze_nutrients(meal_records: List[Dict[str, Any]]) -> Dict[str, Any]:
        """
        영양소를 분석합니다.
        
        Args:
            meal_records: 식사 기록 목록
            
        Returns:
            영양 분석 결과
        """
        # 영양소 분석 로직 구현
        total_nutrients = {
            "calories": 0,
            "protein": 0,
            "carbs": 0,
            "fats": 0
        }
        
        for record in meal_records:
            total_nutrients["calories"] += record.get("calories", 0)
            total_nutrients["protein"] += record.get("protein", 0)
            total_nutrients["carbs"] += record.get("carbs", 0)
            total_nutrients["fats"] += record.get("fat", 0)
        
        return total_nutrients
    
    @staticmethod
    async def get_food_nutrition(food_name: str) -> Dict[str, Any]:
        """
        음식의 영양 정보를 조회합니다.
        
        Args:
            food_name: 음식 이름
            
        Returns:
            영양 정보
        """
        query = """
        SELECT *
        FROM food_nutrition
        WHERE name ILIKE %(food_name)s
        LIMIT 1
        """
        result = await execute_query(query, {"food_name": f"%{food_name}%"})
        
        if result:
            return result[0]
        return {}
    
    # 도구 정의
    @tool("calculate_bmr")
    async def calculate_bmr_tool(gender: str, age: int, weight: float, height: float) -> str:
        """
        기초 대사량(BMR)을 계산합니다.
        
        Args:
            gender: 성별 ('남성' 또는 '여성')
            age: 나이
            weight: 체중(kg)
            height: 신장(cm)
        """
        result = await NutritionTool.calculate_bmr(gender, age, weight, height)
        return f"기초 대사량(BMR): {result} kcal"
    
    @tool("calculate_tdee")
    async def calculate_tdee_tool(bmr: float, activity_level: str) -> str:
        """
        총 일일 에너지 소비량(TDEE)을 계산합니다.
        
        Args:
            bmr: 기초 대사량
            activity_level: 활동 수준 (매우 낮음, 낮음, 보통, 높음, 매우 높음)
        """
        result = await NutritionTool.calculate_tdee(bmr, activity_level)
        return f"총 일일 에너지 소비량(TDEE): {result} kcal"
    
    @tool("calculate_macros")
    async def calculate_macros_tool(tdee: float, goal: str) -> str:
        """
        목표에 따른 거시 영양소 분배를 계산합니다.
        
        Args:
            tdee: 총 일일 에너지 소비량
            goal: 영양 목표 (다이어트, 증량, 유지 등)
        """
        result = await NutritionTool.calculate_macros(tdee, goal)
        return json.dumps(result, ensure_ascii=False)
    
    @tool("calculate_nutrition_plan")
    async def calculate_nutrition_plan_tool(gender: str, age: int, weight: float, height: float, 
                                     activity_level: str, goal: str) -> str:
        """
        영양 계획을 계산합니다.
        
        Args:
            gender: 성별
            age: 나이
            weight: 체중(kg)
            height: 신장(cm)
            activity_level: 활동 수준
            goal: 영양 목표
        """
        result = await NutritionTool.calculate_nutrition_plan(gender, age, weight, height, activity_level, goal)
        return json.dumps(result, ensure_ascii=False)
    
    @tool("get_weekly_meals")
    async def get_weekly_meals_tool(user_id: int) -> str:
        """
        주간 식사 기록을 조회합니다.
        
        Args:
            user_id: 사용자 ID
        """
        result = await NutritionTool.get_weekly_meals(user_id)
        return json.dumps(result, ensure_ascii=False)
    
    @tool("analyze_nutrients")
    async def analyze_nutrients_tool(meal_records: List[Dict[str, Any]]) -> str:
        """
        영양소를 분석합니다.
        
        Args:
            meal_records: 식사 기록 목록
        """
        result = await NutritionTool.analyze_nutrients(meal_records)
        return json.dumps(result, ensure_ascii=False)
    
    @tool("get_food_nutrition")
    async def get_food_nutrition_tool(food_name: str) -> str:
        """
        음식의 영양 정보를 조회합니다.
        
        Args:
            food_name: 음식 이름
        """
        result = await NutritionTool.get_food_nutrition(food_name)
        return json.dumps(result, ensure_ascii=False) 