import React from "react"
import { Link } from "gatsby"

import {
  SearchBox,
  SearchkitProvider,
  SearchkitManager,
  LayoutBody,
  TopBar,
  SideBar,
  LayoutResults,
  ActionBar,
  ActionBarRow,
  RefinementListFilter,
  Hits,
  NoHits,
  HitsStats,
  SearchkitComponent,
  Layout,
  //MovieHitsGridItem,
  SelectedFilters,
  MenuFilter,
  RangeFilter,
  HierarchicalMenuFilter,
  Pagination,
  ResetFilters
} from "searchkit";

import PodcastHit from '../../components/search_partials/PodcastHit.js';

// TODO move to env vars
const API_URL = "http://www.local.test:5000/api/elasticsearch/podcasts_by_language"
const searchkit = new SearchkitManager(API_URL)

// putting in /search folder so gatsby uses that route automatically, and doesn't redirect us to "/"
// (root) when we hit this route over flask
// https://github.com/gatsbyjs/gatsby/issues/20203

const SearchView = (props) => (
  <SearchkitProvider searchkit={searchkit}>
    <Layout>
      <TopBar>
        <SearchBox
          autofocus={true}
          searchOnChange={true}
          prefixQueryFields={props.prefixQueryFields}
        />
      </TopBar>
      <LayoutBody>
        <SideBar>
          <RefinementListFilter
            field="primary_genre"
            title="Primary Genre"
            id="primary-genre"
            operator="AND"
            size={10}
          />
          <RefinementListFilter
            id="genres"
            title="Sub-genres"
            field="genres"
            operator="OR"
            size={10}
          />
          <RefinementListFilter
            id="language"
            title="Language"
            field="language"
            operator="OR"
            size={10}
          />
          <RangeFilter 
            field="episode_count" 
            id="episode-count" 
            min={0} max={1000} 
            showHistogram={true} 
            title="Episode Count"
          />
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
          <Hits mod="sk-hits-grid" hitsPerPage={10} itemComponent={PodcastHit}
          />
          <NoHits/>
        </LayoutResults>
      </LayoutBody>
    </Layout>
  </SearchkitProvider>
)

export default SearchView
