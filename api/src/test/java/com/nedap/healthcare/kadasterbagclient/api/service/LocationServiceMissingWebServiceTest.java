package com.nedap.healthcare.kadasterbagclient.api.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.nedap.healthcare.kadasterbagclient.api.AbstractSpringNoExternalServiceTest;
import com.nedap.healthcare.kadasterbagclient.api.dao.AddressDao;
import com.nedap.healthcare.kadasterbagclient.api.exception.FaildCommunicationWithServer;
import com.nedap.healthcare.kadasterbagclient.api.model.AbstractPersistedEntity;
import com.nedap.healthcare.kadasterbagclient.api.model.Address;
import com.nedap.healthcare.kadasterbagclient.api.model.AddressDTO;
import com.nedap.healthcare.kadasterbagclient.service.ServiceImpl;

import eu.execom.testutil.property.Property;

public class LocationServiceMissingWebServiceTest extends AbstractSpringNoExternalServiceTest {

    @Autowired
    private LocationServiceHelper locationService;
    @Autowired
    private AddressDao locationDao;

    /**
     * Testing {@link LocationServiceHelper#getLocation)} without pre cashed location when web service is missing .
     * 
     * @throws Exception
     */
    @Test
    public void testGetLocationWithoutPreCashedLocationMissingWebServiceEndpoint() throws Exception {

        // data preparation
        final String postalCode = "postcode3";
        final int number = 3;
        final String extension = "a3";

        ServiceImpl.destroy();

        takeSnapshot();

        // call method
        AddressDTO locationDto = null;
        try {
            locationDto = locationService.getAddress(postalCode, number, extension);
            assertTrue(false);
        } catch (final FaildCommunicationWithServer ex) {
            assertTrue(true);
        }

        ServiceImpl.main(null);

        locationDto = locationService.getAddress(postalCode, number, extension);

        // asserting
        final Address createdEntity = locationDao.findByCountryPostalCodeAndNumber(LocationService.NL_COUNTRY_CODE,
                postalCode, number, extension);

        assertObject(createdEntity, Property.notNull(AbstractPersistedEntity.ID),
                Property.notNull(Address.CREATION_DATE),
                Property.changed(Address.COUNTRY_CODE, LocationService.NL_COUNTRY_CODE),
                Property.changed(Address.NUMBER, number), Property.changed(Address.NUMBER_POSTFIX, extension),
                Property.changed(Address.POSTAL_CODE, postalCode), Property.notNull(Address.LATITUDE),
                Property.notNull(Address.LONGITUDE), Property.notNull(Address.VALID_FROM),
                Property.nulll(Address.VALID_TO), Property.notNull(Address.CITY), Property.notNull(Address.STREET));

        assertObject(locationDto, Property.changed(AddressDTO.COUNTRY_CODE, createdEntity.getCountryCode()),
                Property.changed(AddressDTO.NUMBER, createdEntity.getNumber()),
                Property.changed(AddressDTO.NUMBER_POSTFIX, createdEntity.getNumberPostfix()),
                Property.changed(AddressDTO.POSTAL_CODE, createdEntity.getPostalCode()),
                Property.changed(AddressDTO.LATITUDE, createdEntity.getLatitude()),
                Property.changed(AddressDTO.LONGITUDE, createdEntity.getLongitude()),
                Property.changed(AddressDTO.VALID_FROM, createdEntity.getValidFrom()),
                Property.changed(AddressDTO.VALID_TO, createdEntity.getValidTo()),
                Property.changed(AddressDTO.CITY, createdEntity.getCity()),
                Property.changed(AddressDTO.STREET, createdEntity.getStreet()));
    }
}
