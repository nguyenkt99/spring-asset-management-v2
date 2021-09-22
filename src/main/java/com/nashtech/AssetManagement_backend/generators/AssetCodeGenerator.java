package com.nashtech.AssetManagement_backend.generators;


import com.nashtech.AssetManagement_backend.entity.AssetEntity;
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
import java.util.Properties;

public class AssetCodeGenerator extends SequenceStyleGenerator {
    public static final String NUMBER_FORMAT_PARAMETER = "numberFormat";
    public static final String NUMBER_FORMAT_DEFAULT = "%06d";

    private String format;

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
    @Override
    public Serializable generate(SharedSessionContractImplementor session,
                                 Object object) throws HibernateException {
        String prefix = ((AssetEntity) object).getCategoryEntity().getPrefix();

        int l = prefix.length()+ 6;

        String QUERY = "from AssetEntity a where a.id LIKE '"
                + prefix + "%' and length(a.id)='"+l+"' order by a.id desc";


        Query query = session.createQuery(QUERY);
        int id = 1;
        if (query.getResultList().size() > 0) {

            System.out.println("__________________--"+((AssetEntity) query.getResultList().get(0)).getAssetCode());

            if(isNumeric(((AssetEntity) query.getResultList().get(0)).getAssetCode()
                    .replace(prefix, ""))){

                id = Integer.parseInt(((AssetEntity) query.getResultList().get(0)).getAssetCode()
                        .replace(prefix, "")) + 1;
                System.out.println("__________________--"+id);
            }



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
