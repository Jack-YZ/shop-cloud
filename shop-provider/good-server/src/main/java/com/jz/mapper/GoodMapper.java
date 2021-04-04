package com.jz.mapper;


import com.jz.domain.Good;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface GoodMapper {

    @Select("select * from t_goods ")
    List<Good> selectAll();

    @SelectProvider(type=GoodSelectProvider.class,method = "queryByIds")
    List<Good> selectByIds(@Param("ids") List<Long> ids);

    class GoodSelectProvider{
        public String queryByIds(@Param("ids") List<Long> ids){
            StringBuilder sb = new StringBuilder(50);
            sb.append("select * from t_goods where id in (");
            for (int i = 0; i <ids.size() ; i++) {
                if(i==0){
                    sb.append(ids.get(i));
                }else{
                    sb.append(",");
                    sb.append(ids.get(i));
                }
            }
            sb.append(")");
            return sb.toString();
        }
    }
}
