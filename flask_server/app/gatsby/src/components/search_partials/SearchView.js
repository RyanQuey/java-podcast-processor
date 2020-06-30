import React from "react"
import { Link } from "gatsby"
import moment from "moment"

import {
  SearchBox,
  SearchkitProvider,
  SearchkitManager,
  SortingSelector, 
  LayoutBody,
  TopBar,
  SideBar,
  LayoutResults,
  ActionBar,
  ActionBarRow,
  Hits,
  NoHits,
  HitsStats,
  SearchkitComponent,
  Layout,
  //MovieHitsGridItem,
  SelectedFilters,
  Pagination,
  ResetFilters
} from "searchkit";


// TODO move to env vars
const API_URL = "http://www.local.test:5000/api/elasticsearch/"

// putting in /search folder so gatsby uses that route automatically, and doesn't redirect us to "/"
// (root) when we hit this route over flask
// https://github.com/gatsbyjs/gatsby/issues/20203

const SearchView = (props) => (
  <SearchkitProvider searchkit={new SearchkitManager(API_URL + props.esIndex)}>
    <Layout>
      <TopBar>
        <SearchBox
          autofocus={true}
          searchOnChange={true}
          prefixQueryFields={props.prefixQueryFields}
        />
        &nbsp;&nbsp;
          <SortingSelector options={[
						{label: "Relevance", field: "_score", order: "desc", defaultOption:true},
						{label: "Latest Releases", field: "published_date", order:"desc"},
						{label: "Earliest Releases", field:"published_date", order:"asc", key:"earliest"},
						{label: "Highly Rated", key: "rating", fields: [
							{field: "rating", options: {order: "desc"}},
							{field: "prices", options: {order: "asc", "mode" : "avg"}}
						]}
					]}/>
      </TopBar>
      <LayoutBody>
        <SideBar>
          {props.filters}
        </SideBar>
        <LayoutResults>
          <ActionBar>
            <ActionBarRow>
              <HitsStats/>
            </ActionBarRow>

            <ActionBarRow>
              <SelectedFilters/>
              <ResetFilters/>
            </ActionBarRow>
          </ActionBar>
          <Hits 
            mod="sk-hits-grid" 
            hitsPerPage={10} 
            itemComponent={props.hitComponent}
          />
          <NoHits/>
        </LayoutResults>
      </LayoutBody>
      <Pagination showNumbers={true} />
    </Layout>
  </SearchkitProvider>
)

export default SearchView
