import React from "react"
import { Link } from "gatsby"

import Layout from "../../components/layout"
import SearchView from "../../components/search_partials/SearchView"
import Image from "../../components/image"
import SEO from "../../components/seo"
import PodcastHit from '../../components/search_partials/PodcastHit.js';
import {
  HierarchicalMenuFilter,
  RefinementListFilter,
  MenuFilter,
  RangeFilter,
} from "searchkit"

const IndexPage = () => (
  <Layout>
    <SEO title="Home" />
    <SearchView 
      esIndex="podcasts_by_language"
      prefixQueryFields={["name^5", "description^3", "genres^2", "primary_genre^4", "author^5"]}
      hitComponent={PodcastHit}
      filters={
        <div>
          <HierarchicalMenuFilter
            fields={["primary_genre", "genres"]}
            title="Genre"
            id="genre"
            operator="AND"
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
        </div>
      }
    />
    <div style={{ maxWidth: `300px`, margin: `1.45rem` }}>
      <h1>Hi people</h1>
      <p>Welcome to your new Gatsby site.</p>
      <p>Now go build something great.</p>
      <div style={{ maxWidth: `300px`, marginBottom: `1.45rem` }}>
        <Image />
      </div>
    </div>
    <Link to="/search/episode-search/">Search episodes</Link> <br />
    <Link to="/search/page-2/">Go to page 2</Link> <br />
    <Link to="/search/using-typescript/">Go to "Using TypeScript"</Link>
  </Layout>
)

export default IndexPage
