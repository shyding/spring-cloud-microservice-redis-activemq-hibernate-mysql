package com.hzg.user;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/16.
 */
@Entity(name = "user")
public class User  implements Serializable {

    private static final long serialVersionUID = 345435245233221L;

    public User(){
        super();
    }

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="id")
    private Integer id;

    @Column(name="username",length=32)
    private String username;

    @Column(name="age")
    private Integer age;

    @Column(name="nickname",length=32)
    private String nickname;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", age=" + age +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}