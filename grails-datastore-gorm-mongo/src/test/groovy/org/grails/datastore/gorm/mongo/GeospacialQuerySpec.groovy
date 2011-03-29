package org.grails.datastore.gorm.mongo

import grails.gorm.tests.GormDatastoreSpec
import grails.persistence.Entity

class GeospacialQuerySpec extends GormDatastoreSpec {

    static {
        GormDatastoreSpec.TEST_CLASSES << Hotel
    }

    void "Test that we can query within a circle"() {
        given:
            new Hotel(name:"Hilton", location:[50, 50]).save()
            new Hotel(name:"Raddison", location:[150, 130]).save(flush:true)
            session.clear()

        when:
            def h = Hotel.findByLocation([50, 50])

        then:
            h != null

        when:
            h = Hotel.findByLocationWithinCircle([[40, 30],40])

        then:
            h != null
            h.name == "Hilton"
        when:
            h = Hotel.findByLocationWithinCircle([[10, 10],30])

        then:
            h == null
    }

    void "Test that we can query within a box"() {
        given:
            new Hotel(name:"Hilton", location:[50, 50]).save()
            new Hotel(name:"Raddison", location:[150, 130]).save(flush:true)
            session.clear()

        when:
            def h = Hotel.findByLocation([50, 50])

        then:
            h != null

        when:
            h = Hotel.findByLocationWithinBox([[40, 30],[60, 70]])

        then:
            h != null
            h.name == "Hilton"
        when:
            h = Hotel.findByLocationWithinBox([[20, 10],[40, 30]])

        then:
            h == null
    }

    void "Test that we can query for nearby location"() {
        given:
            new Hotel(name:"Hilton", location:[50, 50]).save()
            new Hotel(name:"Raddison", location:[150, 130]).save(flush:true)
            session.clear()

        when:
            def h = Hotel.findByLocation([50, 50])

        then:
            h != null

        when:
            h = Hotel.findByLocationNear([50, 60])

        then:
            h != null
            h.name == "Hilton"
        when:
            h = Hotel.findByLocationNear([170, 160])

        then:
            h != null
            h.name == "Raddison"
    }

    void "Test that we can query for nearby location with criteria"() {
        given:
            new Hotel(name:"Hilton", location:[50, 50]).save()
            new Hotel(name:"Raddison", location:[150, 130]).save(flush:true)
            session.clear()

        when:
            def h = Hotel.findByLocation([50, 50])

        then:
            h != null

        when:
            h = Hotel.createCriteria().get { near( "location", [50, 60] ) }

        then:
            h != null
            h.name == "Hilton"
        when:
            h = Hotel.createCriteria().get { near( "location", [170, 160] ) }

        then:
            h != null
            h.name == "Raddison"
    }
}

@Entity
class Hotel {
    Long id
    String name
    List location

    static mapping = {
        location geoIndex:true
    }
}
