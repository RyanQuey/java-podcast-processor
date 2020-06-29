import React from "react"
import { Link } from "gatsby"

import Layout from "../../components/layout"
import SearchView from "./SearchView"
import Image from "../../components/image"
import SEO from "../../components/seo"

const IndexPage = () => (
  <Layout>
    <SEO title="Home" />
    <SearchView 
      prefixQueryFields={["name^5", "description^3"]}
    />
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
