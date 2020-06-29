import React from "react"
import { Link } from "gatsby"

import Layout from "../../components/layout"
import Image from "../../components/image"
import SEO from "../../components/seo"

// putting in /search folder so gatsby uses that route automatically, and doesn't redirect us to "/"
// (root) when we hit this route over flask
// https://github.com/gatsbyjs/gatsby/issues/20203

const IndexPage = () => (
  <Layout>
    <SEO title="Home" />
    <h1>Hi people</h1>
    <p>Welcome to your new Gatsby site.</p>
    <p>Now go build something great.</p>
    <div style={{ maxWidth: `300px`, marginBottom: `1.45rem` }}>
      <Image />
    </div>
    <Link to="/search/page-2/">Go to page 2</Link> <br />
    <Link to="/search/using-typescript/">Go to "Using TypeScript"</Link>
  </Layout>
)

export default IndexPage
