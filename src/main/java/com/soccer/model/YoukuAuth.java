package com.soccer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by Administrator on 2017/9/10.
 */
@Entity
@Table(name = "youku_auth")
public class YoukuAuth implements java.io.Serializable {

    // Fields

    private String access_token;
    private Date expires_date;
    private Integer expires_in;
    private String refresh_token;
    private String token_type;

    // Constructors

    /** default constructor */
    public YoukuAuth() {
    }

    /** full constructor */
    public YoukuAuth(String access_token, Date expires_date,
                     Integer expires_in, String refresh_token, String token_type) {
        this.access_token = access_token;
        this.expires_date = expires_date;
        this.expires_in = expires_in;
        this.refresh_token = refresh_token;
        this.token_type = token_type;
    }

    // Property accessors

    @Column(name = "access_token", length = 50)
    public String getAccess_token() {
        return this.access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    @Column(name = "expires_date", length = 19)
    public Date getExpires_date() {
        return this.expires_date;
    }

    public void setExpires_date(Date expires_date) {
        this.expires_date = expires_date;
    }

    @Column(name = "expires_in")
    public Integer getExpires_in() {
        return this.expires_in;
    }

    public void setExpires_in(Integer expires_in) {
        this.expires_in = expires_in;
    }

    @Column(name = "refresh_token", length = 50)
    public String getRefresh_token() {
        return this.refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    @Id
    @Column(name = "token_type", length = 50)
    public String getToken_type() {
        return this.token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

}