/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cards.nine.api.collections

import cards.nine.processes.collections.messages._
import org.scalacheck.Shapeless._
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

class ConvertersSpec
  extends Specification
  with ScalaCheck {

  "toApiCreateOrUpdateCollectionResponse" should {
    "convert CreateOrUpdateCollectionResponse to ApiCreateOrUpdateCollectionResponse" in {
      prop { (response: CreateOrUpdateCollectionResponse) ⇒

        val apiResponse = Converters.toApiCreateOrUpdateCollectionResponse(response)

        apiResponse.packagesStats must_== response.packagesStats
        apiResponse.publicIdentifier must_== response.publicIdentifier
      }
    }
  }

  "toApiGetSubscriptionsByUserResponse" should {
    "convert GetSubscriptionsByUserResponse to ApiGetSubscriptionsByUserResponse" in {
      prop { (response: GetSubscriptionsByUserResponse) ⇒

        val apiResponse = Converters.toApiGetSubscriptionsByUser(response)

        apiResponse.subscriptions must_== response.subscriptions
      }
    }
  }

  "toApiIncreaseViewsCountByOneResponse" should {
    "convert IncreaseViewsCountByOneResponse to ApiIncreaseViewsCountByOneResponse" in {
      prop { (response: IncreaseViewsCountByOneResponse) ⇒

        val apiResponse = Converters.toApiIncreaseViewsCountByOneResponse(response)

        apiResponse.publicIdentifier must_== response.publicIdentifier
      }
    }
  }
}
