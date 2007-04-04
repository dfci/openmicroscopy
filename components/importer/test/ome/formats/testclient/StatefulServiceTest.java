/*
 * ome.formats.testclient.StatefulServiceTest
 *
 *------------------------------------------------------------------------------
 *
 *  Copyright (C) 2005 Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee
 *
 *
 *
GPL'd. See License attached to this project
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *------------------------------------------------------------------------------
 */

package ome.formats.testclient;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Configuration;
import org.testng.annotations.Test;

import ome.api.RawPixelsStore;
import ome.formats.importer.ImportLibrary;
import ome.formats.importer.util.TinyImportFixture;
import ome.model.containers.Dataset;
import ome.model.core.Image;
import ome.model.core.Pixels;
import ome.model.enums.Family;
import ome.model.meta.Event;
import ome.model.meta.EventLog;
import ome.parameters.Parameters;
import ome.system.EventContext;
import ome.system.Login;
import ome.system.ServiceFactory;
import ome.util.builders.PojoOptions;
import omeis.providers.re.RenderingEngine;
import omeis.providers.re.codomain.ReverseIntensityContext;
import omeis.providers.re.data.PlaneDef;

import junit.framework.TestCase;

@Test( groups = {"integration","stateful"} )
public class StatefulServiceTest extends TestCase {

	private final static Log log = LogFactory.getLog(StatefulServiceTest.class);
	
	protected TinyImportFixture fixture;
	
	protected ServiceFactory sf;
	
	protected Dataset d;
	
	protected Pixels p;
	
	@Configuration( beforeTestClass = true )
	public void setup() throws Exception {
		super.setUp();		
		sf = new ServiceFactory(new Login("root","ome"));
		fixture = new TinyImportFixture(sf);
		fixture.setUp();
		fixture.doImport();
		d = fixture.getDataset();

		// TODO ImportLibrary.Step.step(int) should be refactored to
		// step(long pixId, int planenum, byte[] plane, int z, int c, int z)
		// then we can simply get the pixId directly from the fixture since
		// it's only importing one image. (could also make step nullable and
		// get rid of SimpleStep (add though, LoggingStep)
		Set<Dataset> set = sf.getPojosService()
		.loadContainerHierarchy(Dataset.class,Collections.singleton(d.getId()),
				new PojoOptions().leaves().map());
		
		Image i = (Image) set.iterator().next().linkedImageList().get(0);
		p = i.getDefaultPixels();
		
		assertNotNull( p );
                assertNotNull( p.getId() );
	}

	@Override
	@Configuration( afterTestClass = true )
	protected void tearDown() throws Exception {
		fixture.tearDown();
		super.tearDown();
	}
	
	/* TODO this needs to be refactored into client.
	 * currently however bioformats-->client rather
	 * than client-->bioformats. need to fix that
	 */
	@Test( groups = {"ticket:326"} )
	public void testTwoCallsToSameStatefulService() throws Exception {

		RenderingEngine re = newRE();
		
		EventContext e0 = re.getCurrentEventContext();
		re.setDefaultZ(0);
		EventContext e1 = re.getCurrentEventContext();
		re.setDefaultZ(1);
		EventContext e2 = re.getCurrentEventContext();

		assertTrue( e0.getCurrentEventId().equals( e1.getCurrentEventId()));
		assertTrue( e2.getCurrentEventId().equals( e1.getCurrentEventId()));
		
	}
	
	@Test( groups = {"ticket:326", "broken", "ticket:379"} )
	public void testEventLogProduction() throws Exception {
		
		RenderingEngine re = newRE();
		long eventId = re.getCurrentEventContext().getCurrentEventId();
		List<EventLog> logs = getLogsForEvent(eventId);
		assertTrue( "Should be empty:"+logs, logs.size() == 0 );

		re.setRGBA(0, 0, 0, 0, 0);
		re.saveCurrentSettings();
		
		logs = getLogsForEvent(eventId);
		assertTrue( logs.size() > 0 );
		
	}
	
	@Test( groups = {"ticket:557"} )
	public void testStatefulServicesCanReauthorize() throws Exception {
		RenderingEngine re = newRE();
		PlaneDef plane = new PlaneDef(0,0);
		plane.setZ(0);
		re.renderAsPackedInt(plane);

		// perform some changing operation
		boolean active = re.isActive(0);
		re.setActive(0, !active);
		re.renderAsPackedInt(plane);
		
		// another changing operation
		boolean noiseReduction = re.getChannelNoiseReduction(0);
		Family family = re.getChannelFamily(0);
		double coefficient = re.getChannelCurveCoefficient(0);
		re.setQuantizationMap(0, family, coefficient, !noiseReduction);
		re.renderAsPackedInt(plane);
	}

	// ~ Helpers
	// =========================================================================
	
	private RenderingEngine newRE() {
		RenderingEngine re = sf.createRenderingEngine();
		re.lookupPixels( p.getId() );
		if (!re.lookupRenderingDef( p.getId() )) {
		    re.resetDefaults();
                    re.lookupRenderingDef(p.getId());
                }
		re.load();
		return re;
	}

	private List<EventLog> getLogsForEvent(long eventId) {
		List<EventLog> logs = sf.getQueryService().findAllByQuery(
				"from EventLog log where log.event.id = :id",
				new Parameters().addId(eventId));
		return logs;
	}

}
