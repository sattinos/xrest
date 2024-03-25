# XRest
A REST accelerator library. It allows for creating CRUD Controller and express conditions in JSON notation

# Why XRest ? Another CRUD Controller ?
The available solutions on the net doesn't offer a powerful expressive way to declare the conditions. <br/>
A truly CRUD Controller, should offer:<br/>
    1. a generic based version that does the heavy lifting workload.<br/>
    2. a fully customizable solution based on business requirements.<br/>
    3. a robust expressive way of WHERE conditions.<br/>

# Prerequisites
1. Java 17 or higher ([Lebrica JDK](https://bell-sw.com/pages/downloads/#jdk-17-lts) is recommended here)
2. [Maven](https://maven.apache.org/download.cgi) 3.9.2 or higher
3. [Spring Boot](https://spring.io/projects/spring-boot) 

# Supported Database
Principally, XRest is supposed to work on any Sql-based database (PgSql, MySql or Microsoft Sql Server). 
But, as the first release, I have included a test project (Web App) in H2 only.

Providing tests for all type of databases is coming soon.

# How to build
mvn clean package

# How to test 
mvn test

# Expressing Where Condition
The Where condition is in JSON notation. It allows you to express a business filter in JSON format.
Whether you need this condition in the API, Service Layer or Infrastructure Layer.

The Structure:
    It can have one of these two forms:

    1. LHS/RHS format:
    {
        "op": ...,
        "lhs": ...,
        "rhs": ...        
    }

    This is used with Binary operators, where:
    op: operator type ( <, =, <=, >, >=, !=, like )
    lhs: left hand side, should be the entity field name.
    rhs: right hand side, should be the value

    example1:
    {
        "op": "like",
        "lhs": "title",
        "rhs": "%Harry Potter%"
    }
    => all entities which have a title similar to the form: %Harry Potter%

    example2:
    {
        "op": ">",
        "lhs": "age",
        "rhs": 18
    }
    => all entities which have an age higher than 18

    If the type of the rhs is not scalar, you need to provide a hint what is it through the "type" key.
    for example:

    example3:
    {
        "op": ">",
        "lhs": "publishDate",
        "rhs": "2009-01-01",
        "type": "Date"
    }
    => all entities whose publishDate is after 2009-01-01

    2. RANGE format:
    This is used with Ternary operators, where:
    op: operator type ( between )
    lhs: left hand side, should be the entity field name.
    range1: the start of the range
    range2: the end of the range
    
    example4:
    {
        "op": "between",
        "lhs": "deathDate",
        "range1": "1999-06-01",
        "range2": "2003-12-01",
        "type": "Date"
    }
    => all entities whose deathDate is in the range inclusive [1999-06-01 , 2003-12-01]

    example5:
    {
        "op": "between",
        "lhs": "age",
        "range1": 18,
        "range2": 28
    }
    => all entities whose age is in the range inclusive [18, 28]
