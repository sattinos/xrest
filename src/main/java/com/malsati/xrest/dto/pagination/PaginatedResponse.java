package com.malsati.xrest.dto.pagination;

import java.util.List;

/*
    This record represent typical paginated response for any API, whether it Mobile API or Admin Portal API
    The main reason for it, is to escape verbose data found in Spring Boot Page<T>
    --------------------

    Example:

    Let's say we have 300 users in DB
    page size is 10
    total pages is 300/10 = 28
    totalItems = 300


    {
      "currentPage": 1,     // The second page
      "pageSize": 10,
      "totalPages": 28,
      "totalUsers": 300,
      "users": [
        {
          "id": 1,
          "username": "user1",
          "email": "user1@example.com"
        },
        {
          "id": 2,
          "username": "user2",
          "email": "user2@example.com"
        },
        // More user objects
      ]
}

*/
public record PaginatedResponse<T> (
        int currentPage,        // First page index is 0
        int pageSize,
        long totalPages,
        long totalItems,
        List<T> data
) {}