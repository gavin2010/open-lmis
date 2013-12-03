/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.GeographicLevel;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.repository.GeographicZoneRepository;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class GeographicZoneServiceTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  GeographicZoneRepository repository;

  @InjectMocks
  GeographicZoneService service;

  public static final String ROOT_GEOGRAPHIC_ZONE_CODE = "Root";
  public static final String ROOT_GEOGRAPHIC_ZONE_NAME = "Root";

  @Test
  public void shouldSaveGeographicZone() throws Exception {
    GeographicLevel rootLevel = new GeographicLevel(1234L, ROOT_GEOGRAPHIC_ZONE_CODE, ROOT_GEOGRAPHIC_ZONE_NAME, 1);
    GeographicZone rootZone = new GeographicZone(1234L, "root zone", "root zone", rootLevel, null);
    GeographicLevel childLevel = new GeographicLevel(2345L, "child level", "child level", 2);
    GeographicZone childZone = new GeographicZone(null, "child zone", "child zone", childLevel, rootZone);

    when(repository.getGeographicLevelByCode(childZone.getLevel().getCode())).thenReturn(childLevel);
    when(repository.getByCode(childZone.getParent().getCode())).thenReturn(rootZone);

    service.save(childZone);

    verify(repository).getGeographicLevelByCode("child level");
    verify(repository).getByCode("root zone");
    assertThat(childZone.getLevel().getId(), is(2345L));
    assertThat(childZone.getParent().getId(), is(1234L));
    verify(repository).save(childZone);
  }

  @Test
  public void shouldThrowAnExceptionIfParentCodeIsInvalid() throws Exception {
    GeographicZone invalidZone = new GeographicZone(null, "invalid zone", "invalid zone", null, null);

    GeographicLevel childLevel = new GeographicLevel(2345L, "child level", "child level", 2);
    GeographicZone childZone = new GeographicZone(null, "child zone", "child zone", childLevel, invalidZone);

    when(repository.getGeographicLevelByCode(childZone.getLevel().getCode())).thenReturn(childLevel);
    when(repository.getByCode(childZone.getParent().getCode())).thenReturn(null);

    expectedEx.expect(dataExceptionMatcher("error.geo.zone.parent.invalid"));

    service.save(childZone);
  }

  @Test
  public void shouldThrowAnExceptionIfGeographicLevelCodeIsInvalid() throws Exception {
    GeographicLevel invalidLevel = new GeographicLevel(null, "invalid level", "invalid level", null);
    GeographicZone childZone = new GeographicZone(1235L, "child zone", "child zone", invalidLevel, null);

    when(repository.getByCode(childZone.getLevel().getCode())).thenReturn(null);

    expectedEx.expect(dataExceptionMatcher("error.geo.level.invalid"));

    service.save(childZone);
  }

  @Test
  public void shouldThrowExceptionIfLevelIsNotRootAndStillParentIsNull() throws Exception {
    GeographicLevel level = new GeographicLevel(1L, "abc", "abc", 2);
    GeographicZone zone = new GeographicZone(1L, "xyz", "xyz", level, null);

    when(repository.getGeographicLevelByCode(zone.getLevel().getCode())).thenReturn(level);

    expectedEx.expect(dataExceptionMatcher("error.invalid.hierarchy"));

    service.save(zone);
  }

  @Test
  public void shouldUpdateZoneIfZonePreviouslyPresent() throws Exception {
    GeographicLevel rootLevel = new GeographicLevel(1234L, ROOT_GEOGRAPHIC_ZONE_CODE, ROOT_GEOGRAPHIC_ZONE_NAME, 1);
    GeographicZone rootZone = new GeographicZone(1234L, "root zone", "root zone", rootLevel, null);
    GeographicLevel childLevel = new GeographicLevel(2345L, "child level", "child level", 2);
    GeographicZone childZone = new GeographicZone(2345L, "child zone", "child zone", childLevel, rootZone);

    when(repository.getGeographicLevelByCode(childZone.getLevel().getCode())).thenReturn(childLevel);
    when(repository.getByCode(childZone.getParent().getCode())).thenReturn(rootZone);

    childZone.setName("new name");
    service.save(childZone);

    verify(repository).save(childZone);
    assertThat(childZone.getLevel(), is(childLevel));
    assertThat(childZone.getParent(), is(rootZone));
  }

  @Test
  public void shouldGetGeoZoneById() throws Exception {
    GeographicZone expected = new GeographicZone(1L, "Root", "Root", null, null);
    when(repository.getById(1L)).thenReturn(expected);

    GeographicZone actual = service.getById(1L);

    assertThat(actual, is(expected));
    verify(repository).getById(1L);
  }

  @Test
  public void shouldThrowExceptionIfParentSpecifiedForTopMostLevelGeographicZone() throws Exception {
    GeographicLevel level = new GeographicLevel(1L, "abc", "abc", 1);
    GeographicZone parent = new GeographicZone(1L, "xyz", "xyz", null, null);
    GeographicZone country = new GeographicZone(1L, "xyz", "xyz", level, parent);

    when(repository.getGeographicLevelByCode(country.getLevel().getCode())).thenReturn(level);
    when(repository.getByCode(country.getParent().getCode())).thenReturn(parent);

    expectedEx.expect(dataExceptionMatcher("error.invalid.hierarchy"));
    service.save(country);
  }

  @Test
  public void shouldThrowExceptionIfParentSetToItsSiblingInHierarchy() throws Exception {
    GeographicLevel level = new GeographicLevel(1L, "abc", "abc", 2);
    GeographicZone country1 = new GeographicZone(1L, "xyz", "xyz", level, null);
    GeographicZone country2 = new GeographicZone(1L, "xyz", "xyz", level, country1);

    when(repository.getGeographicLevelByCode(country2.getLevel().getCode())).thenReturn(level);
    when(repository.getByCode(country2.getParent().getCode())).thenReturn(country1);

    expectedEx.expect(dataExceptionMatcher("error.invalid.hierarchy"));
    service.save(country2);
  }

  @Test
  public void shouldThrowExceptionIfParentIsSetToALowerInHierarchyGeographicLevel() throws Exception {
    GeographicLevel higherLevel = new GeographicLevel(1L, "abc", "abc", 2);
    GeographicLevel lowerLevel = new GeographicLevel(1L, "abc", "abc", 3);
    GeographicZone country1 = new GeographicZone(1L, "xyz", "xyz", lowerLevel, null);
    GeographicZone country2 = new GeographicZone(1L, "xyz", "xyz", higherLevel, country1);

    when(repository.getGeographicLevelByCode(country2.getLevel().getCode())).thenReturn(higherLevel);
    when(repository.getByCode(country2.getParent().getCode())).thenReturn(country1);

    expectedEx.expect(dataExceptionMatcher("error.invalid.hierarchy"));
    service.save(country2);
  }
}
