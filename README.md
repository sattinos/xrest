# XRest
A REST accelerator library. It allows for creating CRUD Controller and express conditions in JSON notation

# Why XRest ? Another CRUD Controller ?
The available solutions on the net doesn't offer a powerful expressive way to declare the conditions. <br/>
A truly CRUD Controller, should offer:<br/>
    1. a generic based version that does the heavy workload.<br/>
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

    example6:
    {
        "op": "&&",
        "lhs": {
            "op": "between",
            "lhs": "publishDate",
            "range1": "1999-06-01",
            "range2": "2003-12-01",
            "type": "Date"
        },
        "rhs": {
            "op": "||",
            "lhs": {
                "op": "like",
                "lhs": "name",
                "rhs": "% of %"
            },
            "rhs": {
                "op": ">",
                "lhs": "noPages",
                "rhs": 800
            }
        }
    }
    => all entities that:
            has been published in the range inclusive [1999-06-01 , 2003-12-01]
            and 
                either 
                       its name is similar to the token " of " 
                    or its number of pages is more than 800

### The CRUD Endpoints
The following CRUD endpoints are supported:<br/>
    1. /getOne  ( condition can be passed ) <br/>
    2. /getMany ( condition can be passed ) <br/>
    3. /count  ( condition can be passed )<br/>
    4. /createOne <br/>
    5. /createMany <br/>
    6. /updateOne <br/>
    7. /updateMany <br/>
    8. /deleteOne <br/>
    9. /deleteMany ( condition should be passed ) <br/>

In this release, only soft delete is supported. In the next release I will add hard delete.

### How to use this library ?

## 1) Design your entity
Make sure you inherit from BaseEntity:

```java
@NoArgsConstructor
@Data
@Entity
@Table(name = "Author")
public class Author extends BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "full_name", unique = true)
    private String fullName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @OneToMany(mappedBy = "author", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Book> books;
}
```

## 2) Design CRUD endpoints DTOs

### CreateOne Endpoint (CreateOneInputDto, CreateOneOutputDto)

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOneAuthorInputDto {
    private String fullName;
    private LocalDate birthDate;
    private Collection<Long> bookIds;
}
```
```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOneAuthorOutputDto extends CreateOneAuthorInputDto {
    private Long id;

    public CreateOneAuthorOutputDto(Long id, String fullName, LocalDate birthDate, Collection<Long> bookIds) {
        super(fullName, birthDate, bookIds);
        this.id = id;
    }
}
```

### UpdateOne Endpoint (UpdateOneInputDto)
```java

public class UpdateOneAuthorInputDto extends CreateOneAuthorOutputDto {  
}
```

### GetOne Endpoint (GetOneInputDto)
```java
public class GetOneAuthorOutputDto extends CreateOneAuthorOutputDto {
}
```

### DeleteOne Endpoint (DeleteOneOutputDto)
```java
public class DeleteOneAuthorOutputDto extends UpdateOneAuthorInputDto {
}
```

## 3) Write down your entity mapper interface that inherits from IMapper

```java
@Mapper(componentModel = "spring")
public interface AuthorMapper extends IMapper<Author,
        Long,
        CreateOneAuthorInputDto,
        CreateOneAuthorOutputDto,
        UpdateOneAuthorInputDto,
        DeleteOneAuthorOutputDto,
        GetOneAuthorOutputDto> {

    @Override
    @Mapping(source = "bookIds", target = "books")
    Author createOneInputDtoToEntity(CreateOneAuthorInputDto createOneAuthorInputDto);
    
    @Override
    @Named("createOne")
    @Mapping(source = "books", target = "bookIds")
    CreateOneAuthorOutputDto entityToCreateOneOutputDto(Author entity);
    
    @Override
    List<Author> createManyInputDtoToEntities(Iterable<CreateOneAuthorInputDto> createManyInputDto);
    
    @Override
    @IterableMapping(qualifiedByName = "createOne")
    List<CreateOneAuthorOutputDto> entitiesToCreateManyOutputDto(List<Author> entities);
    
    @Override
    @Mapping(source = "bookIds", target = "books")
    Author updateOneInputDtoToEntity(UpdateOneAuthorInputDto updateOneAuthorInputDto);
    
    @Override
    @Mapping(source = "books", target = "bookIds")
    DeleteOneAuthorOutputDto entityToDeleteOneOutputDto(Author entity);
    
    @Override
    List<DeleteOneAuthorOutputDto> entitiesToDeleteManyOutputDto(List<Author> entity);
    
    @Override
    @Mapping(source = "books", target = "bookIds")
    GetOneAuthorOutputDto entityToGetOneoutputDto(Author entity);

    default List<Book> mapBookIdsToBooks(Collection<Long> bookIds) {
        var books = new ArrayList<Book>(bookIds.size());
        for (Long id: bookIds) {
            books.add(new Book(id));
        }
        return books;
    }

    default List<Long> mapBooksToBookIds(Collection<Book> books) {
        var bookIds = new ArrayList<Long>(books.size());
        for (var book: books) {
            bookIds.add(book.getId());
        }
        return bookIds;
    }
}
```


## 4) Design your repository: Make sure you inherit from JpaRepository as well as JpaSpecificationExecutor : 
```java
public interface AuthorRepository extends JpaRepository<Author, Long>, JpaSpecificationExecutor<Author> {
    boolean existsByFullName(String name);
}
```

## 5) Write down your service class:
1. It shoud extend CrudServiceORM
2. In its constructor, it should inject the entity 
   repository and the mapper you've created in previous steps.
3. Implement validateCreateOneInput if needed
4. Implement onPreCreateOne if needed
5. Implement validateUpdateOneInput if needed
6. Implement onPreUpdateOne if needed

```java
@Service
public class AuthorsService extends CrudServiceORM<
        Author,
        Long,
        CreateOneAuthorInputDto,
        CreateOneAuthorOutputDto,
        UpdateOneAuthorInputDto,
        DeleteOneAuthorOutputDto,
        GetOneAuthorOutputDto> {
    
    public AuthorsService(
            AuthorRepository authorRepository,
            AuthorMapper mapper) {
        super(authorRepository, mapper);
    }

    @Autowired
    BookRepository bookRepository;    

    @Override
    public ArrayList<AppError> validateCreateOneInput(CreateOneAuthorInputDto createOneAuthorInputDto) {
        // Write down your own validation for the input Dto
    }

    @Override
    protected void onPreCreateOne(CreateOneAuthorInputDto createOneAuthorInputDto, Author entityToCreate) {
        // Write down any Business Specific Logic Here before the entity is saved to DB
    }

    @Override
    public Pair<ArrayList<AppError>, Author> validateUpdateOneInput(UpdateOneAuthorInputDto updateOneAuthorInputDto) {
        // Write down your own validation for the input Dto
    }

    @Override
    protected void onPreUpdateOne(UpdateOneAuthorInputDto updateOneAuthorInputDto, Author author) {
        // Write down any Business Specific Logic Here before the entity is saved to DB
    }
}
```

## 6) Write your CRUD Controller:
1. It should inherit from CRUDController
2. It should pass the service class you've created in previous step

```java
@RequestMapping("/app/author")
@RestController
public class AuthorsController extends CrudController<Author,
        Long,
        CreateOneAuthorInputDto,
        CreateOneAuthorOutputDto,
        UpdateOneAuthorInputDto,
        DeleteOneAuthorOutputDto,
        GetOneAuthorOutputDto> {
    public AuthorsController(AuthorsService authorsService) {
        super(authorsService);
    }
}
```

Before start using the library, I recommend you check the test project inside the test folder.
You will find:
    1. simple_web_app folder : This is a sample web application it contains a practical example of how to use the library.
    2. AuthorControllerTest: All Endpoint test cases. It also has cases of when you can pass JSON condition.

![Class Diagram](assets/classDiagram.png)

