import { get } from "lodash";

import {
    Hits,
    SearchkitComponent,
    HitItemProps
} from "searchkit";
import React from 'react'

const PodcastHit = (props) => (
  <div className={props.bemBlocks.item().mix(props.bemBlocks.container("item"))}>
    <div className={props.bemBlocks.item("title")}>{props.result._source.name}</div>
    <div className={props.bemBlocks.item("details")}>
      <strong>From:</strong>
      <div>{props.result._source.name}</div>
      <strong>To:</strong>
      <div>{props.result._source.name}</div>
    </div>
  </div>
)

export default PodcastHit
