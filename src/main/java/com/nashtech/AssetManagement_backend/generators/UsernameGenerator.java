package com.nashtech.AssetManagement_backend.generators;


import com.nashtech.AssetManagement_backend.entity.UsersEntity;
import org.checkerframework.checker.regex.qual.Regex;
import org.hibernate.Session;
import org.hibernate.tuple.ValueGenerator;

import javax.persistence.FlushModeType;
import javax.persistence.Query;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class UsernameGenerator implements ValueGenerator<String> {


    @Override
    public String generateValue(Session session, Object o) {

        StringBuilder initials = new StringBuilder();
        for (String s : ((UsersEntity) o).getUserDetail().getLastName().split(" ")) {
            initials.append(s.charAt(0));
        }

        String username = ((UsersEntity) o).getUserDetail().getFirstName().toLowerCase() + initials.toString().toLowerCase();


        Query query = session.createQuery("from UsersEntity where userName like :name order by id DESC").setParameter("name", "%" + username + "%").setFlushMode(FlushModeType.COMMIT);

        String us = username.toString();
        List<?> resultList = query.getResultList();
        resultList = resultList.stream().map(e -> (UsersEntity)e)
                .filter(e -> Pattern.compile("^\\d*$").matcher(e.getUserName().replace(us, "")).matches())
                .collect(Collectors.toList());
        int count = resultList.size();
        if (count > 0) {
            String abc = ((UsersEntity) resultList.get(0)).getUserName()
                    .replace(username, "");

            if (abc.length() == 0)
                username += 1;
            else {
                int n = Integer.parseInt(abc) + 1;
                username += n;
            }
        }

        return username;
    }
}
