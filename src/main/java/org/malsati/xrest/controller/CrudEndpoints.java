package org.malsati.xrest.controller;

/**
 * These are the list of CRUD APIs offered by XRest.
 */
public final class CrudEndpoints {
    /**
     * Will allow you to retrieve an entity based on its Id or a JSON condition
     */
    public static final String GET_ONE = "/getOne";
    /**
     * Will allow you to retrieve many entities based on a JSON condition
     */
    public static final String GET_MANY = "/getMany";

    /**
     * Will allow you to retrieve the count of entities that satisfy a JSON condition
     */
    public static final String COUNT = "/count";

    /**
     * Will allow you to create one entity based on the CreateOneDto passed
     */
    public static final String CREATE_ONE = "/createOne";

    /**
     * Will allow you to create many entities at once based on the CreateManyDto passed
     */
    public static final String CREATE_MANY = "/createMany";
    /**
     * Will allow you to update an entity fields that are passed in the UpdateOneDto
     * Notice that the assumption is to ignore null values passed.
     * It will only update the fields that are considered in the Dto.
     */
    public static final String UPDATE_ONE = "/updateOne";

    /**
     * Will allow you to update many entities fields at once that are passed in the UpdateManyDto
     * Notice that the assumption is to ignore null values passed.
     * It will only update the fields that are considered in the Dto.
     */
    public static final String UPDATE_MANY = "/updateMany";


    /**
     * Will allow you to delete an entity by Id
     */
    public static final String DELETE_ONE = "/deleteOne";

    /**
     * Will allow you to delete all the entities that satisfy a JSON condition you pass
     * Hard + Soft delete are supported
     */
    public static final String DELETE_MANY = "/deleteMany";
}
