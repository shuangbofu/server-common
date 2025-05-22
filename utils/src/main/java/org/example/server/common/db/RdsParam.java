package org.example.server.common.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RdsParam implements Serializable {
    private String url;
    private String username;
    private String password;


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        RdsParam rdsParam = (RdsParam) object;
        return Objects.equals(url, rdsParam.url) && Objects.equals(username, rdsParam.username) && Objects.equals(password, rdsParam.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, username, password);
    }
}
