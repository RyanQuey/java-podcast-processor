import React from "react"
import { Link } from "gatsby"

import {
  SearchBox,
  SearchkitProvider,
  SearchkitManager,
  Layout as SearchKitLayout,
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
  //MovieHitsGridItem,
  SelectedFilters,
  MenuFilter,
  HierarchicalMenuFilter,
  Pagination,
  ResetFilters
} from "searchkit";

import PodcastHit from '../../components/search_partials/PodcastHit.js';
import Layout from "../../components/layout"
import Image from "../../components/image"
import SEO from "../../components/seo"

// TODO move to env vars
const API_URL = "http://www.local.test:5000/api/elasticsearch/podcasts_by_language"
const searchkit = new SearchkitManager(API_URL)

// putting in /search folder so gatsby uses that route automatically, and doesn't redirect us to "/"
// (root) when we hit this route over flask
// https://github.com/gatsbyjs/gatsby/issues/20203

const IndexPage = () => (
  <Layout>
    <SEO title="Home" />
    <SearchkitProvider searchkit={searchkit}>
      <SearchKitLayout>
        <TopBar>
          <SearchBox
            autofocus={true}
            searchOnChange={true}
            prefixQueryFields={true ? ["name", "description"] : ["Carrier^3", "Dest^3", "DestCityName^3", "DestCountry^2", "DestRegion^2", "DestAirportID^5", "DestWeather", "PodcastNum^6", "Origin^3", "OriginCityName^3", "OriginCountry^2", "OriginRegion", "OriginAirportID^5", "OriginWeather"]}
          />
        </TopBar>
        <LayoutBody>
          <SideBar>
            <HierarchicalMenuFilter
              fields={["Carrier"]}
              title="Carrier"
              id="carrier"
            />
            <RefinementListFilter
              id="Dest"
              title="Destination"
              field="Dest"
              operator="AND"
              size={10}
            />
            <RefinementListFilter
              id="Origin"
              title="Origin"
              field="Origin"
              operator="AND"
              size={10}
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
      </SearchKitLayout>
    </SearchkitProvider>
    <div style={{ maxWidth: `300px`, margin: `1.45rem` }}>
      <h1>Hi people</h1>
      <p>Welcome to your new Gatsby site.</p>
      <p>Now go build something great.</p>
      <div style={{ maxWidth: `300px`, marginBottom: `1.45rem` }}>
        <Image />
      </div>
    </div>
    <Link to="/search/page-2/">Go to page 2</Link> <br />
    <Link to="/search/using-typescript/">Go to "Using TypeScript"</Link>
  </Layout>
)

export default IndexPage
