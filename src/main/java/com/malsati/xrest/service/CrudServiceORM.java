package com.malsati.xrest.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.malsati.xrest.dto.errors.ErrorCode;
import com.malsati.xrest.dto.pagination.PaginatedResponse;
import com.malsati.xrest.infrastructure.jpql.SpecificationBuilder;
import com.malsati.xrest.mapper.IMapper;
import com.malsati.xrest.mapper.PaginationMapper;
import com.malsati.xrest.utilities.text.StringExtensions;
import com.malsati.xrest.utilities.tuples.Pair;

import jakarta.persistence.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.malsati.xrest.dto.ServiceResponse;
import com.malsati.xrest.dto.errors.AppError;
import com.malsati.xrest.entities.BaseEntity;

public abstract class CrudServiceORM<T extends BaseEntity<TKeyType>,
        TKeyType extends Serializable,
        CreateOneInputDto,
        CreateOneOutputDto,
        UpdateOneInputDto,
        DeleteOneOutputDto,
        GetOneOutputDto> implements CrudService<T, TKeyType, CreateOneInputDto, CreateOneOutputDto, UpdateOneInputDto, DeleteOneOutputDto, GetOneOutputDto> {
    private static String isNotDeletedCondition = "{ \"op\": \"=\", \"lhs\": \"isDeleted\", \"rhs\": false }";
    protected JpaRepository<T, TKeyType> jpaRepository;
    protected IMapper<T, TKeyType, CreateOneInputDto, CreateOneOutputDto, UpdateOneInputDto, DeleteOneOutputDto, GetOneOutputDto> mapper;

    @Autowired
    protected SpecificationBuilder specificationBuilder;

    public CrudServiceORM(JpaRepository jpaRepository,
                          IMapper<T, TKeyType, CreateOneInputDto, CreateOneOutputDto, UpdateOneInputDto, DeleteOneOutputDto, GetOneOutputDto> mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public ServiceResponse<CreateOneOutputDto> createOne(CreateOneInputDto createOneInputDto) {
        var validationResult = validateCreateOneInput(createOneInputDto);
        if (!validationResult.isEmpty()) {
            return new ServiceResponse<>(validationResult);
        }
        T entity = mapper.createOneInputDtoToEntity(createOneInputDto);
        onPreCreateOne(createOneInputDto, entity);
        var res = this.jpaRepository.save(entity);
        var createdOutputDto = mapper.entityToCreateOneOutputDto(res);
        return new ServiceResponse(createdOutputDto);
    }

    protected abstract void onPreCreateOne(CreateOneInputDto createOneInputDto, T entityToCreate);
    protected abstract void onPreUpdateOne(UpdateOneInputDto updateOneInputDto, T entityToUpdate);

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
        for (var createOneInputDto: createManyInputDto) {
            var e =  entities.get(index);
            onPreCreateOne(createOneInputDto, e);
            index++;
        }
    }

    @Override
    public ServiceResponse<Boolean> updateOne(UpdateOneInputDto updateOneInputDto) {
        var validationResult = validateUpdateOneInput(updateOneInputDto);
        if(validationResult == null) {
            // Need to implement validateUpdateOneInput in a subclass
            return new ServiceResponse<>(false);
        }
        if (!validationResult.first().isEmpty()) {
            return new ServiceResponse<>(validationResult.first());
        }
        var entity = validationResult.second();
        onPreUpdateOne(updateOneInputDto, entity);
        this.jpaRepository.save(entity);
        return new ServiceResponse<>(true);
    }

    @Override
    public ServiceResponse<Boolean> updateMany(Iterable<UpdateOneInputDto> updateManyInputDto) {
        var allValidations = validateUpdateManyInput(updateManyInputDto);
        var entities = new ArrayList<T>();
        for(var validation: allValidations) {
            if( validation.second() == null) {
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
        for (var updateOneInputDto: updateManyInputDto) {
            var entity =  entities.get(index);
            onPreUpdateOne(updateOneInputDto, entity);
            index++;
        }
    }

    public abstract ArrayList<AppError> validateCreateOneInput(CreateOneInputDto createOneInputDto);

    public abstract Pair<ArrayList<AppError>, T> validateUpdateOneInput(UpdateOneInputDto updateOneInputDto);

    private ArrayList<AppError> validateCreateManyInput(Iterable<CreateOneInputDto> createManyInputDto) {
        var allValidations = new ArrayList<AppError>();
        for (var createOneInputDto : createManyInputDto) {
            var validationResult = validateCreateOneInput(createOneInputDto);
            if (validationResult != null) {
                allValidations.addAll(validationResult);
            }
        }
        return allValidations;
    }

    private ArrayList<Pair<ArrayList<AppError>, T>> validateUpdateManyInput(Iterable<UpdateOneInputDto> updateManyInputDto) {
        var allValidations = new ArrayList<Pair<ArrayList<AppError>, T>>();
        for (var updateOneInputDto : updateManyInputDto) {
            var validationResult = validateUpdateOneInput(updateOneInputDto);
            allValidations.add(validationResult);
            if( validationResult.second() == null) {
                return allValidations;
            }
        }
        return allValidations;
    }

    @Override
    public ServiceResponse<GetOneOutputDto> getOne(String condition) {
        if( condition == null || condition.isBlank() || StringExtensions.IsBlankJson(condition)) {
            return new ServiceResponse<GetOneOutputDto>(new AppError(ErrorCode.InvalidInput, "bad JSON condition."));
        }
        var specificationExecutor = (JpaSpecificationExecutor<T>) jpaRepository;
        if (specificationExecutor == null) {
            return new ServiceResponse<GetOneOutputDto>(new AppError(ErrorCode.InternalSystemError, "unable to find JpaSpecificationExecutor"));
        }
        var criteria = specificationBuilder.buildWithAnd(condition, isNotDeletedCondition);
        Optional<T> entity = specificationExecutor.findOne(criteria);
        if( entity.isPresent() ) {
            var outputDto = mapper.entityToGetOneoutputDto(entity.get());
            return new ServiceResponse<GetOneOutputDto>(outputDto);
        }
        return new ServiceResponse<GetOneOutputDto>(new AppError(ErrorCode.NotFound, "no results matched such condition"));
    }

    @Override
    public ServiceResponse<GetOneOutputDto> getOneById(TKeyType id) {
        if( id == null) {
            return new ServiceResponse<GetOneOutputDto>(new AppError(ErrorCode.RequiredField, "required field: id."));
        }
        var entity = jpaRepository.findById(id);
        if( entity.isPresent() ) {
            var outputDto = mapper.entityToGetOneoutputDto(entity.get());
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
        if( condition == null || condition.isBlank() || StringExtensions.IsBlankJson(condition)) {
            criteria = specificationBuilder.build(isNotDeletedCondition);
        }
        if( criteria == null ) {
            criteria = specificationBuilder.buildWithAnd(condition, isNotDeletedCondition);
        }
        onePage = specificationExecutor.findAll(criteria, pageable);
        PaginatedResponse<GetOneOutputDto> paginatedResponse = PaginationMapper.mapPageToPaginatedResponse(onePage, mapper::entityToGetOneoutputDto);
        return new ServiceResponse<>(paginatedResponse);
    }

    @Override
    public ServiceResponse<Long> count(String condition) {
        var specificationExecutor = (JpaSpecificationExecutor<T>) jpaRepository;
        if (specificationExecutor == null) {
            return new ServiceResponse(new AppError(ErrorCode.InternalSystemError, "unable to find JpaSpecificationExecutor"));
        }

        Specification<T> criteria = null;
        if( condition == null || condition.isBlank() || StringExtensions.IsBlankJson(condition)) {
            criteria = specificationBuilder.build(isNotDeletedCondition);
        }
        if( criteria == null ) {
            criteria = specificationBuilder.buildWithAnd(condition, isNotDeletedCondition);
        }
        return new ServiceResponse<>(specificationExecutor.count(criteria));
    }

    @Override
    public ServiceResponse<DeleteOneOutputDto> deleteOneById(TKeyType id) {
        if( id == null) {
            return new ServiceResponse<DeleteOneOutputDto>(new AppError(ErrorCode.RequiredField, "required field: id."));
        }
        var entityRef = jpaRepository.findById(id);
        if( entityRef.isPresent() ) {
            var entity = entityRef.get();
            if( entity.getIsDeleted() ) {
                return new ServiceResponse<DeleteOneOutputDto>(new AppError(ErrorCode.AlreadyDeleted, "the entity is already deleted", id));
            }
            entity.setIsDeleted(true);
            jpaRepository.save(entity);

            var deleteOneOutputDto = mapper.entityToDeleteOneOutputDto(entity);

            return new ServiceResponse<>(deleteOneOutputDto);
        }
        return new ServiceResponse<DeleteOneOutputDto>(new AppError(ErrorCode.NotFound, "invalid id value", id));
    }

    @Override
    public ServiceResponse<List<DeleteOneOutputDto>> deleteMany(String condition) {
        if( condition == null || condition.isBlank() || StringExtensions.IsBlankJson(condition)) {
            return new ServiceResponse<List<DeleteOneOutputDto>>(new AppError(ErrorCode.InvalidInput, "condition is required"));
        }
        var specificationExecutor = (JpaSpecificationExecutor<T>) jpaRepository;
        if (specificationExecutor == null) {
            return new ServiceResponse(new AppError(ErrorCode.InternalSystemError, "unable to find JpaSpecificationExecutor"));
        }
        var criteria = specificationBuilder.buildWithAnd(condition, isNotDeletedCondition);

        List<T> entities = specificationExecutor.findAll(criteria);

        var deleteManyOutputDto = mapper.entitiesToDeleteManyOutputDto(entities);
        for (var entity: entities) {
            entity.setIsDeleted(true);
        }
        jpaRepository.saveAll(entities);
        if( entities.size() == 0 ) {
            return new ServiceResponse(new AppError(ErrorCode.NotFound, "nothing was deleted."));
        }
        return new ServiceResponse<>(deleteManyOutputDto);
    }
}