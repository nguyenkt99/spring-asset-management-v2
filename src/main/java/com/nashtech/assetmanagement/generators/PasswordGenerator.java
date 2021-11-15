package com.nashtech.assetmanagement.generators;

import com.nashtech.assetmanagement.entity.UserEntity;
import org.hibernate.Session;
import org.hibernate.tuple.ValueGenerator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.FlushModeType;
import javax.persistence.Query;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PasswordGenerator implements ValueGenerator<String> {
    @Override
    public String generateValue(Session session, Object o) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        StringBuilder initials = new StringBuilder();
        for (String s : ((UserEntity) o).getUserDetail().getLastName().split(" ")) {
            initials.append(s.charAt(0));
        }

        String dateOfBirth =((UserEntity) o).getUserDetail().getDateOfBirth().format(DateTimeFormatter.ofPattern("ddMMyyyy"));
        String username = ((UserEntity) o).getUserDetail().getFirstName().toLowerCase() + initials.toString().toLowerCase();
        Query query = session.createQuery("from UserEntity where userName like :name order by id DESC")
                .setParameter("name", "%" + username + "%").setFlushMode(FlushModeType.COMMIT);

        String us = username.toString();
        List<?> resultList = query.getResultList();
        resultList = resultList.stream().map(e -> (UserEntity)e)
                .filter(e -> Pattern.compile("^\\d*$").matcher(e.getUserName().replace(us, "")).matches())
                .collect(Collectors.toList());
        int count = resultList.size();
        if (count > 0) {
            String suffix = ((UserEntity) resultList.get(0)).getUserName()
                    .replace(username, "");

            if (suffix.length() == 0)
                username += 1;
            else {
                int n = Integer.parseInt(suffix) + 1;
                username += n;
            }
        }

        return encoder.encode(username + "@" + dateOfBirth);
    }
}
