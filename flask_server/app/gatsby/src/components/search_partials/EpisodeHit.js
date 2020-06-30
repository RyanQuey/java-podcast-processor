import { get, truncate } from "lodash";
import { Link } from "gatsby"

import {
    Hits,
    SearchkitComponent,
    HitItemProps
} from "searchkit";
import React from 'react'
import moment from "moment"

// some are array, some string. Must be from an old search before I made all array?
const showKeywords = (keywords) => {
  if (!keywords) {
    return "(None)"
  } else if (typeof keywords === "string") {
    keywords = keywords.split(" ")
  }

  return keywords.join(", ")
}

const EpisodeHit = (props) => (
  <div className={props.bemBlocks.item().mix(props.bemBlocks.container("item"))}>
    <img className={props.bemBlocks.item("poster")} src={props.result._source.image_url}/>
    <h3><a href={props.result._source.episode_url} target="_blank" className={props.bemBlocks.item("title")}>{props.result._source.title || "(No Title)"}</a></h3>
    <div className={props.bemBlocks.item("details")}>
      <div>{truncate(props.result._source.summary, {length: 100})}</div>
      <div><strong>Author:</strong> {props.result._source.author}</div>
      <div><strong>Keywords:</strong> {showKeywords(props.result._source.keywords)}</div>
      <div><strong>Published:</strong> {moment(props.result._source.published_date).format("MMMM Do, YYYY")}</div>
      {false && <Link to={`/search/episode-search/?podcast_api_id[0]=${props.result._source.podcast_api_id}`}>More from this podcast</Link>}
    </div>
  </div>
)

export default EpisodeHit
