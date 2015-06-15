/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.report.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.report.mapper.lookup.GeographicZoneReportMapper;
import org.openlmis.report.model.GeoZoneReportingRate;
import org.openlmis.report.response.OpenLmisResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
@PrepareForTest(OpenLmisResponse.class)
public class GeoDataControllerTest {

    public static final Long userId = 1L;

  @Mock
  private GeographicZoneReportMapper mapper;

  @InjectMocks
  private GeoDataController controller;
    private MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
  @Before
  public void setup(){
    initMocks(this);
      MockHttpSession mockHttpSession = new MockHttpSession();
      httpServletRequest.setSession(mockHttpSession);
      mockHttpSession.setAttribute(USER, USER);
      mockHttpSession.setAttribute(USER_ID, userId);
      MockitoAnnotations.initMocks(this);
  }

  @Test
  public void shouldGetReportingRateReport() throws Exception {
    List<GeoZoneReportingRate> reportData = new ArrayList<GeoZoneReportingRate>();
    reportData.add(new GeoZoneReportingRate());
    when(mapper.getGeoReportingRate(1l,1L, 1L)).thenReturn(reportData);

    OpenLmisResponse response = new OpenLmisResponse();
    response.addData("map", reportData);
    ResponseEntity<OpenLmisResponse> expectResponse = new ResponseEntity<>(response, HttpStatus.OK);

    ResponseEntity<OpenLmisResponse> actual = controller.getReportingRateReport(1L, 1L,null);

    verify(mapper).getGeoReportingRate(1l,1L, 1L);
  }
}
