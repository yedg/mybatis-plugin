package com.tqlab.demo.dal.dataobject;

import java.io.Serializable;

public class AdminKey implements Serializable {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ADMIN.ID
     *
     * @mbggenerated
     */
    private Integer id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table ADMIN
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ADMIN.ID
     *
     * @return the value of ADMIN.ID
     *
     * @mbggenerated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ADMIN.ID
     *
     * @param id the value for ADMIN.ID
     *
     * @mbggenerated
     */
    public void setId(Integer id) {
        this.id = id;
    }
}