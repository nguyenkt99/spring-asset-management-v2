package com.nashtech.assetmanagement.generators;

import com.nashtech.assetmanagement.entity.UserEntity;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;


public class StaffCodeGenerator extends SequenceStyleGenerator {
    public static final String NUMBER_FORMAT_PARAMETER = "numberFormat";
    public static final String NUMBER_FORMAT_DEFAULT = "%04d";
    private String format;

    @Override
    public Serializable generate(SharedSessionContractImplementor session,
                                 Object object) throws HibernateException {

        String prefix = "SD";
        String QUERY = "from UserEntity u order by u.id desc";
        Query query = session.createQuery(QUERY);
        int id = 1;

        List<?> resultList = query.getResultList();
        if (resultList.size() > 0) {
            id = Integer.parseInt(String.valueOf(((UserEntity) resultList.get(0)).getStaffCode()).replace(prefix , "")) + 1;
        }

        return prefix + String.format(format, id);
    }

    @Override
    public void configure(Type type, Properties params,
                          ServiceRegistry serviceRegistry) throws MappingException {
        super.configure(LongType.INSTANCE, params, serviceRegistry);
        format = ConfigurationHelper.getString(NUMBER_FORMAT_PARAMETER, params, NUMBER_FORMAT_DEFAULT);
    }

}