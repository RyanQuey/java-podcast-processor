import { get } from "lodash";
import { Link } from "gatsby"

import {
    Hits,
    SearchkitComponent,
    HitItemProps
} from "searchkit";
import React from 'react'

const PodcastHit = (props) => (
  <div className={props.bemBlocks.item().mix(props.bemBlocks.container("item"))}>
    <img className={props.bemBlocks.item("poster")} src={props.result._source.image_url100}/>
    <a href={props.result._source.api_url} target="_blank" className={props.bemBlocks.item("title")}>{props.result._source.name}</a>
    <div className={props.bemBlocks.item("details")}>
      <strong>{props.result._source.genres.join(", ")}</strong>
      <div>Total Episodes: {props.result._source.episode_count}</div>
      {false && <Link to={`/search/episode-search/?podcast_api_id[0]=${props.result._source.api_id}`}>Search Episodes from this podcast</Link>}
    </div>
  </div>
)

export default PodcastHit
