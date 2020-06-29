import { get } from "lodash";

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
    </div>
  </div>
)

export default PodcastHit
