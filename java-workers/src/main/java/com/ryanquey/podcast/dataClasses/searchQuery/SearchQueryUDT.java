package com.ryanquey.podcast.dataClasses.searchQuery;

import static com.datastax.oss.driver.api.mapper.annotations.SchemaHint.TargetElement.UDT;
import com.datastax.oss.driver.api.mapper.annotations.SchemaHint;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;

import com.ryanquey.podcast.helpers.DataClassesHelpers;

import java.time.Instant;
import java.lang.IllegalAccessException;

import java.beans.Introspector;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;

import java.lang.reflect.InvocationTargetException;

import com.google.common.base.CaseFormat;

/* 
 * just some minimal, identifying information for this search query
 * to be used as UDT on other tables
 *
 */

@Entity
@CqlName("search_query")
@SchemaHint(targetElement = UDT)
public class SearchQueryUDT {

  public String term;
  public String searchType;
  public String externalApi = "itunes";

  // empty constructor is necessary for DAO
  public SearchQueryUDT() {}
  
  // build from a full SearchQuery instance
  public SearchQueryUDT(SearchQuery searchQuery) {
    DataClassesHelpers.copyMatchingFields(searchQuery, this);
  }

  public String getTerm() {
    return term;
  }

  public void setTerm(String term) {
    this.term = term;
  }

  public String getSearchType() {
    return searchType;
  }

  public void setSearchType(String searchType) {
    this.searchType = searchType;
  }

  public String getExternalApi() {
    return externalApi;
  }

  public void setExternalApi(String externalApi) {
    this.externalApi = externalApi;
  }

  // TODO make this a helper or put in a UDT superclass or something
  // even better, find something in the java driver that does this automatically
  // makes it a string that can be put into CQL
  public String toCQLString () throws IntrospectionException, IllegalAccessException, InvocationTargetException {
		// TODO should I use a stringbuilder?
		String cql = "{";
		// https://stackoverflow.com/a/3334217/6952495
		BeanInfo beanInfo = Introspector.getBeanInfo(SearchQueryUDT.class);

    for (int i = 0; i < beanInfo.getPropertyDescriptors().length; i++) {
      PropertyDescriptor propertyDesc = beanInfo.getPropertyDescriptors()[i]; 

      String propertyName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, propertyDesc.getName());
      Object value = propertyDesc.getReadMethod().invoke(this);

      if (propertyName.equals("class")) {
        // there is a property called "class" that we want to skip
        continue;
      }
      cql = cql + propertyName + ": '" + value + "'";

      if (i + 1 < beanInfo.getPropertyDescriptors().length) {
        // don't want that final comma
        cql = cql + ", ";
      }
      
    }
    cql = cql + "}";

    return cql;
  }
}


