package dao;

import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;

/*
 *
 * https://github.com/datastax/java-driver/tree/4.x/manual/mapper
 *
 * How to get: 
 *  InventoryMapper inventoryMapper = InventoryMapper.builder(session).build();
 *  PodcastDao dao = inventoryMapper.podcastDao("podcast_analysis_tool", "podcasts_by_language");
 *  dao.findById("this-is-the-podcast-id");
 *  
 *  OR 
 *
 *  dao.save(new Product(UUID.randomUUID(), "Mechanical keyboard"));
 *
 *
 *
 * Note that "The mapper maintains an interface cache. Calling a factory method with the same arguments will yield the same DAO instance"
 *
 *
 *
 */
@Mapper
public interface InventoryMapper {
  @DaoFactory
  PodcastDao podcastDao(@DaoKeyspace CqlIdentifier keyspace);

  // helper so can use the inventoryMapper more easily.
  // https://github.com/datastax/java-driver/tree/4.x/manual/mapper/mapper#mapper-builder
  static MapperBuilder<InventoryMapper> builder(CqlSession session) {
    return new InventoryMapperBuilder(session);
  }

}
