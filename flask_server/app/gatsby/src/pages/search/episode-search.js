import React from "react"
import { Link } from "gatsby"

import Layout from "../../components/layout"
import SearchView from "../../components/search_partials/SearchView"
import Image from "../../components/image"
import SEO from "../../components/seo"
import EpisodeHit from '../../components/search_partials/EpisodeHit.js';

import {
  HierarchicalMenuFilter,
  RefinementListFilter,
  MenuFilter,
  RangeFilter,
} from "searchkit"
const EpisodeSearchPage = () => (
  <Layout>
    <SEO title="Home" />
    <SearchView 
      esIndex="episodes_by_podcast"
      prefixQueryFields={["title^5", "description^3", "keywords^2"]}
      hitComponent={EpisodeHit}
      filters={
        <div>
          <RefinementListFilter
            id="keywords"
            title="Keywords"
            field="keywords"
            operator="AND"
            size={10}
          />
          <RangeFilter 
            field="published_date" 
            id="published-date" 
            min={0} max={1000} 
            showHistogram={true} 
            title="Published Date"
          />
        </div>
      }
    />
    <Link to="/search/">Search Podcasts</Link> <br />
    <Link to="/search/using-typescript/">Go to "Using TypeScript"</Link>
  </Layout>
)

export default EpisodeSearchPage
