package me.maiz.project.eduk15boss.model;

import java.io.Serializable;

public class Role implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column boss_role.role_id
     *
     * @mbg.generated Mon May 31 16:20:22 CST 2021
     */
    private Integer roleId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column boss_role.role_name
     *
     * @mbg.generated Mon May 31 16:20:22 CST 2021
     */
    private String roleName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column boss_role.role_value
     *
     * @mbg.generated Mon May 31 16:20:22 CST 2021
     */
    private String roleValue;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table boss_role
     *
     * @mbg.generated Mon May 31 16:20:22 CST 2021
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column boss_role.role_id
     *
     * @return the value of boss_role.role_id
     *
     * @mbg.generated Mon May 31 16:20:22 CST 2021
     */
    public Integer getRoleId() {
        return roleId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column boss_role.role_id
     *
     * @param roleId the value for boss_role.role_id
     *
     * @mbg.generated Mon May 31 16:20:22 CST 2021
     */
    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column boss_role.role_name
     *
     * @return the value of boss_role.role_name
     *
     * @mbg.generated Mon May 31 16:20:22 CST 2021
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column boss_role.role_name
     *
     * @param roleName the value for boss_role.role_name
     *
     * @mbg.generated Mon May 31 16:20:22 CST 2021
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName == null ? null : roleName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column boss_role.role_value
     *
     * @return the value of boss_role.role_value
     *
     * @mbg.generated Mon May 31 16:20:22 CST 2021
     */
    public String getRoleValue() {
        return roleValue;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column boss_role.role_value
     *
     * @param roleValue the value for boss_role.role_value
     *
     * @mbg.generated Mon May 31 16:20:22 CST 2021
     */
    public void setRoleValue(String roleValue) {
        this.roleValue = roleValue == null ? null : roleValue.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table boss_role
     *
     * @mbg.generated Mon May 31 16:20:22 CST 2021
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", roleId=").append(roleId);
        sb.append(", roleName=").append(roleName);
        sb.append(", roleValue=").append(roleValue);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}