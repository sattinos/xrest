package org.malsati.xrest.service;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.malsati.xrest.dto.errors.ErrorCode;
import org.malsati.xrest.dto.pagination.PaginatedResponse;
import org.malsati.xrest.entities.audit.interfaces.DeletionInfo;
import org.malsati.xrest.entities.audit.interfaces.IdentityInfo;
import org.malsati.xrest.infrastructure.jpql.SpecificationBuilder;
import org.malsati.xrest.mapper.IMapper;
import org.malsati.xrest.mapper.PaginationMapper;
import org.malsati.xrest.utilities.text.StringExtensions;
import org.malsati.xrest.utilities.tuples.Pair;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.malsati.xrest.dto.ServiceResponse;
import org.malsati.xrest.dto.errors.AppError;

public class CrudServiceORM<T,
        TKeyType extends Serializable,
        CreateOneInputDto,
        CreateOneOutputDto,
        UpdateOneInputDto extends IdentityInfo<TKeyType>,
        DeleteOneOutputDto,
        GetOneOutputDto> implements CrudService<T, TKeyType, CreateOneInputDto, CreateOneOutputDto, UpdateOneInputDto, DeleteOneOutputDto, GetOneOutputDto> {
    private static final String isNotDeletedCondition = "{ \"op\": \"=\", \"lhs\": \"deleted\", \"rhs\": false }";
    protected JpaRepository<T, TKeyType> jpaRepository;
    protected IMapper<T, TKeyType, CreateOneInputDto, CreateOneOutputDto, UpdateOneInputDto, DeleteOneOutputDto, GetOneOutputDto> mapper;

    @Autowired
    protected SpecificationBuilder<T> specificationBuilder;

    protected boolean isSoftDelete = false;

    public CrudServiceORM(JpaRepository<T, TKeyType> jpaRepository,
                          IMapper<T, TKeyType, CreateOneInputDto, CreateOneOutputDto, UpdateOneInputDto, DeleteOneOutputDto, GetOneOutputDto> mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
        this.isSoftDelete = calculateIsSoftDeleteSupported();
    }

    @Override
    public ServiceResponse<CreateOneOutputDto> createOne(CreateOneInputDto createOneInputDto) {
        var validationResult = validateCreateOneInput(createOneInputDto);
        if (validationResult != null && !validationResult.isEmpty()) {
            return new ServiceResponse<>(validationResult);
        }
        T entity = mapper.createOneInputDtoToEntity(createOneInputDto);
        onPreCreateOne(createOneInputDto, entity);
        var res = this.jpaRepository.save(entity);
        var createdOutputDto = mapper.entityToCreateOneOutputDto(res);
        return new ServiceResponse<CreateOneOutputDto>(createdOutputDto);
    }

    protected void onPreCreateOne(CreateOneInputDto createOneInputDto, T entityToCreate) {}
    protected void onPreUpdateOne(UpdateOneInputDto updateOneInputDto, T entityToUpdate) {}

    protected void onPreDeleteOne(T entityToDelete) {}

    private void onPreDeleteMany(List<T> entities) {
        for (var entity: entities) {
            onPreDeleteOne(entity);
        }
    }

    @Override
    public ServiceResponse<CreateOneOutputDto[]> createMany(Iterable<CreateOneInputDto> createManyInputDto) {
        var allValidations = validateCreateManyInput(createManyInputDto);
        if (!allValidations.isEmpty()) {
            return new ServiceResponse<>(allValidations);
        }
        List<T> entities = mapper.createManyInputDtoToEntities(createManyInputDto);
        onPreCreateMany(createManyInputDto, entities);
        this.jpaRepository.saveAll(entities);
        var createManyOutputDto = mapper.entitiesToCreateManyOutputDto(entities);
        return new ServiceResponse(createManyOutputDto);
    }

    private void onPreCreateMany(Iterable<CreateOneInputDto> createManyInputDto, List<T> entities) {
        int index = 0;
        for (var createOneInputDto : createManyInputDto) {
            var e = entities.get(index);
            onPreCreateOne(createOneInputDto, e);
            index++;
        }
    }

    /*
     * The basic validation when updating any entity.
     * @param updateOneInputDto : The update input Dto, should have implemented IdentityInfo<>
     * @return (null, errorValidation) in case of any validation error
     *         (entity, null) in case of validation success, the entity will be returned as a result as well
     */
    private Pair<ArrayList<AppError>, T> basicValidateUpdateOneInputDto(UpdateOneInputDto updateOneInputDto) {
        if (updateOneInputDto.getId() == null) {
            var error = new AppError(ErrorCode.RequiredField, "required field: id.");
            return new Pair<>(new ArrayList<>(List.of(error)) , null);
        }
        T entity = jpaRepository.findById(updateOneInputDto.getId()).orElse(null);
        if (entity == null) {
            var error = new AppError(ErrorCode.InvalidInput, "wrong id value", updateOneInputDto.getId());
            return new Pair<>(new ArrayList<>(List.of(error)), null);
        }
        return new Pair<>(null, entity);
    }

    @Transactional
    @Override
    public ServiceResponse<Boolean> updateOne(UpdateOneInputDto updateOneInputDto) {
        var basicValidationResult = basicValidateUpdateOneInputDto(updateOneInputDto);
        if( basicValidationResult.second() == null) {
            return new ServiceResponse<Boolean>(basicValidationResult.first());
        }
        T entity = basicValidationResult.second();
        var validationResult = validateUpdateOneInput(updateOneInputDto, entity);
        if (validationResult != null && !validationResult.isEmpty()) {
            return new ServiceResponse<>(validationResult);
        }
        mapper.updateOneInputDtoToEntity(updateOneInputDto, entity);

        onPreUpdateOne(updateOneInputDto, entity);

        this.jpaRepository.save(entity);
        return new ServiceResponse<>(true);
    }

    @Override
    public ServiceResponse<Boolean> updateMany(Iterable<UpdateOneInputDto> updateManyInputDto) {
        var allValidations = validateUpdateManyInput(updateManyInputDto);
        var entities = new ArrayList<T>();
        for (var validation : allValidations) {
            if (validation.second() == null) {
                return new ServiceResponse<>(validation.first());
            }
            entities.add(validation.second());
        }
        onPreUpdateMany(updateManyInputDto, entities);
        jpaRepository.saveAll(entities);
        return new ServiceResponse<>(true);
    }

    private void onPreUpdateMany(Iterable<UpdateOneInputDto> updateManyInputDto, List<T> entities) {
        int index = 0;
        for (var updateOneInputDto : updateManyInputDto) {
            var entity = entities.get(index);
            onPreUpdateOne(updateOneInputDto, entity);
            index++;
        }
    }

    /*
        Returns an empty array of AppError of validation passes
        Otherwise, it will contain list of validation errors
    * */
    public ArrayList<AppError> validateCreateOneInput(CreateOneInputDto createOneInputDto) {
        return null;
    }

    public ArrayList<AppError> validateUpdateOneInput(UpdateOneInputDto updateOneInputDto, T entity) {
        return null;
    }

    private ArrayList<AppError> validateCreateManyInput(Iterable<CreateOneInputDto> createManyInputDto) {
        var allValidations = new ArrayList<AppError>();
        for (var createOneInputDto : createManyInputDto) {
            var validationResult = validateCreateOneInput(createOneInputDto);
            if (validationResult != null && !validationResult.isEmpty()) {
                allValidations.addAll(validationResult);
                return allValidations;
            }
        }
        return allValidations;
    }

    private ArrayList<Pair<ArrayList<AppError>, T>> validateUpdateManyInput(Iterable<UpdateOneInputDto> updateManyInputDto) {
        var allValidations = new ArrayList<Pair<ArrayList<AppError>, T>>();
        for (var updateOneInputDto : updateManyInputDto) {
            var result = basicValidateUpdateOneInputDto(updateOneInputDto);
            allValidations.add(result);
            if( result.first() != null) {
                return allValidations;
            }

            var validationResult = validateUpdateOneInput(updateOneInputDto, result.second());
            if(validationResult != null && !validationResult.isEmpty()) {
                result.first().addAll(validationResult);
                return allValidations;
            }
        }
        return allValidations;
    }

    @Override
    public ServiceResponse<GetOneOutputDto> getOne(String condition) {
        if (condition == null || condition.isBlank() || StringExtensions.IsBlankJson(condition)) {
            return new ServiceResponse<GetOneOutputDto>(new AppError(ErrorCode.InvalidInput, "bad JSON condition."));
        }
        var specificationExecutor = (JpaSpecificationExecutor<T>) jpaRepository;
        if (specificationExecutor == null) {
            return new ServiceResponse<GetOneOutputDto>(new AppError(ErrorCode.InternalSystemError, "unable to find JpaSpecificationExecutor"));
        }
        var criteria = isSoftDelete ? specificationBuilder.buildWithAnd(condition, isNotDeletedCondition) :
                                      specificationBuilder.build(condition);

        Optional<T> entity = specificationExecutor.findOne(criteria);
        if (entity.isPresent()) {
            var outputDto = mapper.entityToGetOneOutputDto(entity.get());
            return new ServiceResponse<GetOneOutputDto>(outputDto);
        }
        return new ServiceResponse<GetOneOutputDto>(new AppError(ErrorCode.NotFound, "no results matched such condition"));
    }

    @Override
    public ServiceResponse<GetOneOutputDto> getOneById(TKeyType id) {
        if (id == null) {
            return new ServiceResponse<GetOneOutputDto>(new AppError(ErrorCode.RequiredField, "required field: id."));
        }
        var entity = jpaRepository.findById(id);
        if (entity.isPresent()) {
            var outputDto = mapper.entityToGetOneOutputDto(entity.get());
            return new ServiceResponse<>(outputDto);
        }
        return new ServiceResponse<GetOneOutputDto>(new AppError(ErrorCode.NotFound, "invalid id value", id));
    }

    @Override
    public ServiceResponse<PaginatedResponse<GetOneOutputDto>> getMany(String condition, Pageable pageable) {
        Page<T> onePage = null;
        var specificationExecutor = (JpaSpecificationExecutor<T>) jpaRepository;
        if (specificationExecutor == null) {
            return new ServiceResponse(new AppError(ErrorCode.InternalSystemError, "unable to find JpaSpecificationExecutor"));
        }

        Specification<T> criteria = null;
        if (condition == null || condition.isBlank() || StringExtensions.IsBlankJson(condition)) {
            if (!isSoftDelete) {
                onePage = jpaRepository.findAll(pageable);
            }

            if (isSoftDelete) {
                criteria = specificationBuilder.build(isNotDeletedCondition);
                onePage = specificationExecutor.findAll(criteria, pageable);
            }
        }

        if (criteria == null && onePage == null) {
            criteria = isSoftDelete ? specificationBuilder.buildWithAnd(condition, isNotDeletedCondition) :
                    specificationBuilder.build(condition);
            onePage = specificationExecutor.findAll(criteria, pageable);
        }

        PaginatedResponse<GetOneOutputDto> paginatedResponse = PaginationMapper.mapPageToPaginatedResponse(onePage, mapper::entityToGetOneOutputDto);
        return new ServiceResponse<>(paginatedResponse);
    }

    @Override
    public ServiceResponse<Long> count(String condition) {
        var specificationExecutor = (JpaSpecificationExecutor<T>) jpaRepository;
        if (specificationExecutor == null) {
            return new ServiceResponse<Long>(new AppError(ErrorCode.InternalSystemError, "unable to find JpaSpecificationExecutor"));
        }

        Specification<T> criteria = null;
        if (condition == null || condition.isBlank() || StringExtensions.IsBlankJson(condition)) {
            if (!isSoftDelete) {
                return new ServiceResponse<>(jpaRepository.count());
            }
            criteria = specificationBuilder.build(isNotDeletedCondition);
        }
        if (criteria == null) {
            criteria = isSoftDelete ? specificationBuilder.buildWithAnd(condition, isNotDeletedCondition) :
                    specificationBuilder.build(condition);
        }
        return new ServiceResponse<>(specificationExecutor.count(criteria));
    }

    @Override
    @Transactional
    public ServiceResponse<DeleteOneOutputDto> deleteOneById(TKeyType id) {
        if (id == null) {
            return new ServiceResponse<DeleteOneOutputDto>(new AppError(ErrorCode.RequiredField, "required field: id."));
        }
        var entityRef = jpaRepository.findById(id);
        if (entityRef.isPresent()) {
            var entity = entityRef.get();

            if (isSoftDelete) {
                var deleteEntity = (DeletionInfo) entity;
                if (deleteEntity.getDeleted()) {
                    return new ServiceResponse<DeleteOneOutputDto>(new AppError(ErrorCode.AlreadyDeleted, "the entity is already deleted", id));
                }
                onPreDeleteOne(entity);
                deleteEntity.setDeleted(true);
                jpaRepository.save(entity);
            }
            if (!isSoftDelete) {
                onPreDeleteOne(entity);
                jpaRepository.deleteById(id);
            }
            var deleteOneOutputDto = mapper.entityToDeleteOneOutputDto(entity);
            return new ServiceResponse<>(deleteOneOutputDto);
        }
        return new ServiceResponse<DeleteOneOutputDto>(new AppError(ErrorCode.NotFound, "invalid id value", id));
    }

    @Override
    public ServiceResponse<List<DeleteOneOutputDto>> deleteMany(String condition) {
        if (condition == null || condition.isBlank() || StringExtensions.IsBlankJson(condition)) {
            return new ServiceResponse<List<DeleteOneOutputDto>>(new AppError(ErrorCode.InvalidInput, "condition is required"));
        }
        var specificationExecutor = (JpaSpecificationExecutor<T>) jpaRepository;
        if (specificationExecutor == null) {
            return new ServiceResponse<List<DeleteOneOutputDto>>(new AppError(ErrorCode.InternalSystemError, "unable to find JpaSpecificationExecutor"));
        }

        var criteria = isSoftDelete ? specificationBuilder.buildWithAnd(condition, isNotDeletedCondition) :
                specificationBuilder.build(condition);

        List<T> entities = specificationExecutor.findAll(criteria);
        if (entities.isEmpty()) {
            return new ServiceResponse<List<DeleteOneOutputDto>>(new AppError(ErrorCode.NotFound, "nothing was deleted."));
        }
        if (!isSoftDelete) {
            onPreDeleteMany(entities);
            jpaRepository.deleteAll(entities);
        }

        if (isSoftDelete) {
            for (var entity : entities) {
                var deleteEntity = (DeletionInfo) entity;
                if (deleteEntity.getDeleted()) {
                    return new ServiceResponse<List<DeleteOneOutputDto>>(
                            new AppError(ErrorCode.AlreadyDeleted, String.format("the entity is already deleted: %s", entity))
                    );
                }
                onPreDeleteOne(entity);
                deleteEntity.setDeleted(true);
                deleteEntity.setDeletedAt(LocalDate.now());
                // TODO: find a way to set deletedBy
            }
            jpaRepository.saveAll(entities);
        }
        var deleteManyOutputDto = mapper.entitiesToDeleteManyOutputDto(entities);
        return new ServiceResponse<>(deleteManyOutputDto);
    }

    private Class<?> getEntityType() {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<?>) parameterizedType.getActualTypeArguments()[0];
    }

    /*
        Warning: This function is Reflection based, it should be called only once at the constructor phase
                 In order to Guarantee That, make sure the Service class that subclass from this class
                 is annotated with @Service
                 It will scope this service as Singleton.
    */
    private boolean calculateIsSoftDeleteSupported() {
        Class<?> entityClass = getEntityType();
        return DeletionInfo.class.isAssignableFrom(entityClass);
    }
}