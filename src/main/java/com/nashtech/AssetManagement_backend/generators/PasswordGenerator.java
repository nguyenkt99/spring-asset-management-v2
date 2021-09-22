package com.nashtech.AssetManagement_backend.generators;

import com.nashtech.AssetManagement_backend.entity.UsersEntity;
import org.hibernate.Session;
import org.hibernate.tuple.ValueGenerator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.FlushModeType;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class PasswordGenerator implements ValueGenerator<String> {
    @Override
    public String generateValue(Session session, Object o) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        StringBuilder initials = new StringBuilder();
        for (String s : ((UsersEntity) o).getUserDetail().getLastName().split(" ")) {
            initials.append(s.charAt(0));
        }

        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
        String dateOfBirth = formatter.format(((UsersEntity) o).getUserDetail().getDateOfBirth());
        String username = ((UsersEntity) o).getUserDetail().getFirstName().toLowerCase() + initials.toString().toLowerCase();
        Query query = session.createQuery("from UsersEntity where userName like :name order by id DESC")
                .setParameter("name", "%" + username + "%").setFlushMode(FlushModeType.COMMIT);

//        int count = query.getResultList().size();
//        if (count > 0) {
//            String abc = ((UsersEntity) query.getResultList().get(0)).getUserName()//vietvb1  / viet van ba
//                    //vietv// viet van
//                    .replace(username, "");//b1
//            if (abc.length() == 0)
//                username += 1;
//            else {
//                int n = Integer.parseInt(abc) + 1;//=>loi
//                username += n;
//            }
//        }

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

        return encoder.encode(username + "@" + dateOfBirth);
    }
}
