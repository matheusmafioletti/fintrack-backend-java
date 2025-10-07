#!/bin/bash

# FinTrack API Test Script
# This script tests the main API endpoints

BASE_URL="http://localhost:8080/api"
TOKEN=""

echo "🚀 FinTrack API Test Script"
echo "================================"
echo ""

# Test 1: Health Check
echo "1️⃣ Testing Health Check..."
curl -s http://localhost:8080/actuator/health | jq '.'
echo ""

# Test 2: Register User
echo "2️⃣ Registering new user..."
REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "password": "password123"
  }')

echo "$REGISTER_RESPONSE" | jq '.'
TOKEN=$(echo "$REGISTER_RESPONSE" | jq -r '.token')
echo ""
echo "✅ Token received: ${TOKEN:0:20}..."
echo ""

# Test 3: Get Current User
echo "3️⃣ Getting current user info..."
curl -s -X GET "$BASE_URL/users/me" \
  -H "Authorization: Bearer $TOKEN" | jq '.'
echo ""

# Test 4: List Categories (should have 11 default)
echo "4️⃣ Listing categories..."
curl -s -X GET "$BASE_URL/categories" \
  -H "Authorization: Bearer $TOKEN" | jq '. | length'
echo " default categories created"
echo ""

# Test 5: Create Transaction
echo "5️⃣ Creating transaction..."
curl -s -X POST "$BASE_URL/transactions" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "description": "Test Transaction",
    "amount": 100.00,
    "type": "EXPENSE",
    "categoryId": 1,
    "date": "2025-10-07"
  }' | jq '.'
echo ""

# Test 6: Get Transaction Summary
echo "6️⃣ Getting transaction summary..."
curl -s -X GET "$BASE_URL/transactions/summary" \
  -H "Authorization: Bearer $TOKEN" | jq '.'
echo ""

# Test 7: Create Budget
echo "7️⃣ Creating budget..."
curl -s -X POST "$BASE_URL/budgets" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "categoryId": 1,
    "amount": 500.00,
    "period": "MONTHLY",
    "startDate": "2025-10-01"
  }' | jq '.'
echo ""

# Test 8: Get Budget Progress
echo "8️⃣ Getting budget progress..."
curl -s -X GET "$BASE_URL/budgets/progress" \
  -H "Authorization: Bearer $TOKEN" | jq '.'
echo ""

# Test 9: Get Financial Overview
echo "9️⃣ Getting financial overview..."
curl -s -X GET "$BASE_URL/reports/overview" \
  -H "Authorization: Bearer $TOKEN" | jq '.'
echo ""

echo "================================"
echo "✅ All tests completed!"
echo "================================"
