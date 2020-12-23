package org.openapitools.api;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.catalina.User;
import org.openapitools.jackson.nullable.JsonNullable;
import org.openapitools.model.UserEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.Valid;
import javax.validation.constraints.*;

@Data
public class CreateUpdateUserRequest {

    @Size(max=256)
    @JsonProperty("username")
    private String username;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @javax.validation.constraints.Email
    @JsonProperty("email")
    private String email;

    @JsonProperty("phone")
    private String phone;

    public UserEntity toUserEntity() {
        UserEntity userEntity =  new UserEntity();
        userEntity.email(this.email);
        userEntity.username(this.username);
        userEntity.firstName(this.firstName);
        userEntity.lastName(this.lastName);
        userEntity.phone(this.phone);
        return userEntity;
    }

//    /**
//     * Get id
//     * @return id
//     */
//    @ApiModelProperty(value = "")
//
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public User username(String username) {
//        this.username = username;
//        return this;
//    }
//
//    /**
//     * Get username
//     * @return username
//     */
//    @ApiModelProperty(value = "")
//
//    @Size(max=256)
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public User firstName(String firstName) {
//        this.firstName = firstName;
//        return this;
//    }
//
//    /**
//     * Get firstName
//     * @return firstName
//     */
//    @ApiModelProperty(value = "")
//
//
//    public String getFirstName() {
//        return firstName;
//    }
//
//    public void setFirstName(String firstName) {
//        this.firstName = firstName;
//    }
//
//    public User lastName(String lastName) {
//        this.lastName = lastName;
//        return this;
//    }
//
//    /**
//     * Get lastName
//     * @return lastName
//     */
//    @ApiModelProperty(value = "")
//
//
//    public String getLastName() {
//        return lastName;
//    }
//
//    public void setLastName(String lastName) {
//        this.lastName = lastName;
//    }
//
//    public User email(String email) {
//        this.email = email;
//        return this;
//    }
//
//    /**
//     * Get email
//     * @return email
//     */
//    @ApiModelProperty(value = "")
//
//    @javax.validation.constraints.Email
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public User phone(String phone) {
//        this.phone = phone;
//        return this;
//    }

//    /**
//     * Get phone
//     * @return phone
//     */
//    @ApiModelProperty(value = "")
//
//
//    public String getPhone() {
//        return phone;
//    }
//
//    public void setPhone(String phone) {
//        this.phone = phone;
//    }


//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (o == null || getClass() != o.getClass()) {
//            return false;
//        }
//        User user = (User) o;
//        return Objects.equals(this.id, user.id) &&
//                Objects.equals(this.username, user.username) &&
//                Objects.equals(this.firstName, user.firstName) &&
//                Objects.equals(this.lastName, user.lastName) &&
//                Objects.equals(this.email, user.email) &&
//                Objects.equals(this.phone, user.phone);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id, username, firstName, lastName, email, phone);
//    }
//
//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("class User {\n");
//
//        sb.append("    id: ").append(toIndentedString(id)).append("\n");
//        sb.append("    username: ").append(toIndentedString(username)).append("\n");
//        sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
//        sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
//        sb.append("    email: ").append(toIndentedString(email)).append("\n");
//        sb.append("    phone: ").append(toIndentedString(phone)).append("\n");
//        sb.append("}");
//        return sb.toString();
//    }

//    /**
//     * Convert the given object to string with each line indented by 4 spaces
//     * (except the first line).
//     */
//    private String toIndentedString(Object o) {
//        if (o == null) {
//            return "null";
//        }
//        return o.toString().replace("\n", "\n    ");
//    }
}

