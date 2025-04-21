"""
데이터베이스 유틸리티 모듈
"""

import os
import sys
from typing import Dict, Any, List, Optional, Tuple
import psycopg2
from psycopg2.extras import RealDictCursor
from dotenv import load_dotenv

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

async def execute_query(query: str, params: Optional[tuple] = None) -> List[Dict[str, Any]]:
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

def fetch_one(query: str, params: Dict[str, Any] = None) -> Optional[Dict[str, Any]]:
    """쿼리를 실행하고 첫 번째 결과만 반환합니다."""
    results = execute_query(query, params)
    if results:
        return results[0]
    return None

def close_all_connections():
    """모든 데이터베이스 연결을 닫습니다."""
    global connection_pool
    if connection_pool:
        connection_pool.closeall()
        connection_pool = None
        print("모든 데이터베이스 연결이 닫혔습니다.")

def test_connection():
    """
    데이터베이스 연결을 테스트합니다.
    """
    try:
        pool = get_connection_pool()
        if pool:
            conn = pool.getconn()
            with conn.cursor() as cur:
                cur.execute("SELECT 1")
                result = cur.fetchone()
                print(f"데이터베이스 연결 테스트 성공: {result}")
                return True
            pool.putconn(conn)
        return False
    except Exception as e:
        print(f"데이터베이스 연결 테스트 실패: {str(e)}")
        return False 