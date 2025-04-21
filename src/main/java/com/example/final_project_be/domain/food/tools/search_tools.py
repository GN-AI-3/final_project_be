"""
검색 도구 모듈
"""

from typing import Dict, Any, List
from langchain_core.messages import HumanMessage, AIMessage
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser
from langchain_core.runnables import RunnablePassthrough
from langchain_core.tools import tool

from .db_utils import execute_query

class SearchTool:
    """검색 관련 도구"""
    
    @tool
    async def search_food(self, food_name: str) -> str:
        """
        음식을 검색합니다.
        
        Args:
            food_name: 음식 이름
            
        Returns:
            검색 결과
        """
        try:
            # 음식 검색
            query = """
            SELECT *
            FROM foods
            WHERE name ILIKE %(food_name)s
            LIMIT 5
            """
            
            result = await execute_query(
                query,
                {"food_name": f"%{food_name}%"}
            )
            
            if not result:
                return f"{food_name}에 대한 검색 결과가 없습니다."
                
            return f"검색 결과: {[food['name'] for food in result]}"
            
        except Exception as e:
            return f"음식 검색 중 오류가 발생했습니다: {str(e)}"
            
    @tool
    async def web_search_food(self, food_name: str) -> str:
        """
        웹에서 음식을 검색합니다.
        
        Args:
            food_name: 음식 이름
            
        Returns:
            검색 결과
        """
        try:
            # 웹 검색 결과 (실제로는 웹 API를 사용해야 함)
            return f"{food_name}에 대한 웹 검색 결과가 없습니다."
            
        except Exception as e:
            return f"웹 검색 중 오류가 발생했습니다: {str(e)}" 