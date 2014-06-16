/* Copyright 2014 Rick Warren
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package crud.http;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Locale;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;

import org.junit.Test;

import com.sun.jersey.api.client.AsyncWebResource;


public class ClientRequestTest {

    @Test
    public void testUpdateResource() {
        // given:
        final AsyncWebResource.Builder mockResourceBuilder = mock(AsyncWebResource.Builder.class);

        final MediaType expectedContentType = MediaType.APPLICATION_SVG_XML_TYPE;
        final MediaType expectedAcceptType = MediaType.TEXT_HTML_TYPE;
        final Locale expectedLanguage = Locale.CANADA_FRENCH;
        final Cookie expectedCookie = new Cookie("hello", "goodbye");
        final String expectedHeaderName = "MyExpectedHeader";
        final String expectedHeaderValue = "MyExpectedHeaderValue";
        final String expectedEntityBody = "MyExpectedEntityBody";

        final ClientRequest request = ClientRequest.newBuilder()
                .acceptedLanguage(expectedLanguage)
                .acceptedMediaType(expectedAcceptType)
                .contentType(expectedContentType)
                .cookie(expectedCookie)
                .entity(expectedEntityBody)
                .header(expectedHeaderName, expectedHeaderValue)
                .build();

        // when:
        request.updateResource(mockResourceBuilder);

        // then:
        verify(mockResourceBuilder).acceptLanguage(expectedLanguage);
        verify(mockResourceBuilder).accept(expectedAcceptType);
        verify(mockResourceBuilder).type(expectedContentType);
        verify(mockResourceBuilder).cookie(expectedCookie);
        verify(mockResourceBuilder).entity(expectedEntityBody);
        verify(mockResourceBuilder).header(expectedHeaderName, expectedHeaderValue);
    }

}
