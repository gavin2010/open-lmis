/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.controller;

import org.openlmis.core.domain.Pagination;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.service.SupplyLineService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * This controller handles endpoint to search supervisory nodes.
 */
@Controller
@RequestMapping(value = "/supplyLines")
public class SupplyLineController extends BaseController {

  public static final String SUPPLY_LINES = "supplyLines";
  public static final String PAGINATION = "pagination";

  @Autowired
  private SupplyLineService service;

  @RequestMapping(value = "/search", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SUPPLY_LINE')")
  public ResponseEntity<OpenLmisResponse> search(@RequestParam(value = "searchParam") String searchParam,
                                                 @RequestParam(value = "columnName") String columnName,
                                                 @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                 @Value("${search.page.size}") String limit) {

    Pagination pagination = new Pagination(page, Integer.parseInt(limit));
    pagination.setTotalRecords(service.getTotalSearchResultCount(searchParam, columnName));
    List<SupplyLine> supplyLines = service.search(searchParam, columnName, pagination);
    ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.response(SUPPLY_LINES, supplyLines);
    response.getBody().addData(PAGINATION, pagination);
    return response;
  }
}
