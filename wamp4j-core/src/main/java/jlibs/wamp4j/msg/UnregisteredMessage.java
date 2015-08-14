/**
 * Copyright 2015 Santhosh Kumar Tekuri
 *
 * The JLibs authors license this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package jlibs.wamp4j.msg;

import com.fasterxml.jackson.databind.node.ArrayNode;
import jlibs.wamp4j.error.InvalidMessageException;

/**
 * If the Broker is able to fulfill and allow the subscription,
 * it answers by sending a SUBSCRIBED message to the Subscriber
 *
 * NOTE: In case of receiving a SUBSCRIBE message from the same Subscriber and
 * to already subscribed topic, Broker should answer with SUBSCRIBED message,
 * containing the existing Subscription|id
 *
 * @author Santhosh Kumar Tekuri
 */
public class UnregisteredMessage extends WAMPMessage{
    public static final int ID = 67;

    /**
     * the ID from the original UNREGISTER request
     */
    public final long requestID;

    public UnregisteredMessage(long requestID){
        this.requestID = requestID;
    }

    @Override
    public int getID(){
        return ID;
    }

    @Override
    public void toArrayNode(ArrayNode array){
        array.add(idNodes[ID]);
        array.add(requestID);
    }

    static final Decoder decoder = new Decoder(){
        @Override
        public WAMPMessage decode(ArrayNode array) throws InvalidMessageException{
            if(array.size()!=2)
                throw new InvalidMessageException();

            assert id(array)==ID;
            return new UnregisteredMessage(longValue(array, 1));
        }
    };
}
