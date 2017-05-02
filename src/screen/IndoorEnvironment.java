package screen;

import static java.util.Arrays.asList;
import static target.Factory.apply;
import static vector.Arithmetic.subtract;
import static vector.Factory.create;
import static vector.Property.magnitude;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import domain.Domain;
import domain.FloorGraph;
import processing.core.PImage;
import target.Target;

/**
 * This class was created to remove specific graph creation from general
 * decision making.
 * 
 * @author Jacob Malter
 *
 * @param <A>
 *            any action
 */
public abstract class IndoorEnvironment<A> extends DecisionMaking<A> {

	/** The width of the display window in units of pixels. */
	private static final int WIDTH = 800;
	/** The height of the display window in units of pixels. */
	private static final int HEIGHT = 800;
	/** The diameter of the drawn ellipses. */
	private static final float ELLIPSE_DIAMETER = 20f;

	/** The radius of satisfaction for targets. */
	private static final float ARRIVE_SATISFACTION = 15;
	/** The radius of deceleration for targets. */
	private static final float ARRIVE_DECELERATION = 75;
	/** Creates targets with the applied radius of satisfaction. */
	private static final BiFunction<Float, Float, Target> TARGET = (x, y) -> {
		return apply(ARRIVE_SATISFACTION, ARRIVE_DECELERATION).apply(create(x, y));
	};

	/** PImage to set as background (must be same size as the sketch window) */
	private final PImage background = loadImage(sketchPath() + "\\src\\screen\\jacob's grid thing.png");

	/**
	 * Create domain from variable arity arguments.
	 * 
	 * @param targets
	 *            Array of target data structures.
	 * @return A domain containing the given targets.
	 */
	private static Domain<Target> domain(Target... targets) {
		return new Domain<Target>(targets);
	}

	/**
	 * @return A small graph mapping the indoor environment.
	 */
	private static FloorGraph<Target, Float> graph() {
		// hall domains
		Domain<Target> hall = domain(TARGET.apply(285f, 475f), TARGET.apply(335f, 475f), TARGET.apply(385f, 475f),
				TARGET.apply(435f, 475f), TARGET.apply(285f, 525f), TARGET.apply(335f, 525f), TARGET.apply(385f, 525f),
				TARGET.apply(435f, 525f), TARGET.apply(285f, 575f), TARGET.apply(335f, 575f), TARGET.apply(385f, 575f),
				TARGET.apply(435f, 575f));
		Domain<Target> hallNorth = domain(TARGET.apply(285f, 325f), TARGET.apply(335f, 325f), TARGET.apply(285f, 375f),
				TARGET.apply(335f, 375f), TARGET.apply(385f, 375f), TARGET.apply(285f, 425f), TARGET.apply(335f, 425f),
				TARGET.apply(385f, 425f), TARGET.apply(435f, 425f));
		Domain<Target> hallNorthNorth = domain(TARGET.apply(200f, 250f), TARGET.apply(250f, 250f),
				TARGET.apply(200f, 300f), TARGET.apply(250f, 300f), TARGET.apply(300f, 300f));
		Domain<Target> hallNorthEast = domain(TARGET.apply(325f, 290f), TARGET.apply(290f, 260f),
				TARGET.apply(325f, 260f), TARGET.apply(365f, 265f));
		Domain<Target> hallSouth = domain(TARGET.apply(335f, 625f), TARGET.apply(385f, 625f), TARGET.apply(435f, 625f),
				TARGET.apply(385f, 675f), TARGET.apply(435f, 675f), TARGET.apply(435f, 725f));
		Domain<Target> hallSouthSouth = domain(TARGET.apply(335f, 675f), TARGET.apply(335f, 725f),
				TARGET.apply(385f, 725f), TARGET.apply(335f, 775f), TARGET.apply(385f, 775f), TARGET.apply(435f, 775f));

		// guest bedroom (near the bottom right corner)
		Domain<Target> guestbedroom = domain(TARGET.apply(550f, 600f), TARGET.apply(600f, 600f),
				TARGET.apply(650f, 600f), TARGET.apply(700f, 600f), TARGET.apply(750f, 600f), TARGET.apply(550f, 640f),
				TARGET.apply(600f, 640f), TARGET.apply(650f, 640f), TARGET.apply(700f, 640f), TARGET.apply(750f, 640f),
				TARGET.apply(550f, 680f), TARGET.apply(600f, 680f), TARGET.apply(650f, 680f), TARGET.apply(700f, 680f),
				TARGET.apply(750f, 680f), TARGET.apply(550f, 720f), TARGET.apply(600f, 720f), TARGET.apply(650f, 720f),
				TARGET.apply(700f, 720f), TARGET.apply(750f, 720f), TARGET.apply(550f, 760f), TARGET.apply(600f, 760f),
				TARGET.apply(650f, 760f), TARGET.apply(700f, 760f), TARGET.apply(750f, 760f));
		Domain<Target> guestbedroomEntrance = domain(TARGET.apply(500f, 685f));

		// guest bathroom (bathroom connected to guest bedroom)
		Domain<Target> guestbathroom = domain(TARGET.apply(485f, 535f), TARGET.apply(555f, 555f),
				TARGET.apply(535f, 535f), TARGET.apply(575f, 535f), TARGET.apply(590f, 505f));
		Domain<Target> guestbathroomShower = domain(TARGET.apply(625f, 535f), TARGET.apply(675f, 505f),
				TARGET.apply(675f, 535f));
		Domain<Target> guestshower = domain(TARGET.apply(725f, 545f), TARGET.apply(775f, 545f),
				TARGET.apply(725f, 475f), TARGET.apply(775f, 475f), TARGET.apply(750f, 510f));

		// kitchen and laundry rooms (near the bottom left corner)
		Domain<Target> kitchen = domain(TARGET.apply(235f, 590f), TARGET.apply(75f, 625f), TARGET.apply(125f, 625f),
				TARGET.apply(175f, 625f), TARGET.apply(225f, 625f), TARGET.apply(275f, 625f), TARGET.apply(75f, 675f),
				TARGET.apply(125f, 675f), TARGET.apply(175f, 675f), TARGET.apply(225f, 675f), TARGET.apply(275f, 675f),
				TARGET.apply(150f, 685f));
		Domain<Target> laundry = domain(TARGET.apply(150f, 715f), TARGET.apply(75f, 775f), TARGET.apply(125f, 775f),
				TARGET.apply(175f, 775f), TARGET.apply(225f, 775f), TARGET.apply(75f, 725f), TARGET.apply(125f, 725f),
				TARGET.apply(175f, 725f), TARGET.apply(225f, 725f));

		// master bedroom (near the top right corner)
		Domain<Target> bedroomNorth = domain(TARGET.apply(375f, 35f), TARGET.apply(410f, 35f), TARGET.apply(460f, 35f),
				TARGET.apply(510f, 35f), TARGET.apply(560f, 35f), TARGET.apply(610f, 35f), TARGET.apply(660f, 35f),
				TARGET.apply(370f, 85f), TARGET.apply(410f, 85f), TARGET.apply(460f, 85f), TARGET.apply(510f, 85f),
				TARGET.apply(560f, 85f), TARGET.apply(610f, 85f), TARGET.apply(660f, 85f), TARGET.apply(370f, 135f),
				TARGET.apply(410f, 135f), TARGET.apply(460f, 135f), TARGET.apply(510f, 135f), TARGET.apply(560f, 135f),
				TARGET.apply(610f, 135f), TARGET.apply(660f, 135f), TARGET.apply(370f, 185f), TARGET.apply(410f, 185f),
				TARGET.apply(460f, 185f), TARGET.apply(510f, 185f), TARGET.apply(560f, 185f), TARGET.apply(610f, 185f),
				TARGET.apply(660f, 185f));
		Domain<Target> bedroomEast = domain(TARGET.apply(710f, 35f), TARGET.apply(760f, 35f), TARGET.apply(710f, 85f),
				TARGET.apply(760f, 85f), TARGET.apply(710f, 135f), TARGET.apply(760f, 135f), TARGET.apply(710f, 185f),
				TARGET.apply(760f, 185f));
		Domain<Target> bedroomSouth = domain(TARGET.apply(360f, 235f), TARGET.apply(410f, 235f),
				TARGET.apply(460f, 235f), TARGET.apply(510f, 235f), TARGET.apply(560f, 235f), TARGET.apply(610f, 235f),
				TARGET.apply(660f, 235f), TARGET.apply(365f, 265f), TARGET.apply(410f, 285f), TARGET.apply(460f, 285f),
				TARGET.apply(510f, 270f), TARGET.apply(560f, 275f), TARGET.apply(610f, 285f));
		Domain<Target> bedroomNorthWest = domain(TARGET.apply(375f, 35f), TARGET.apply(345f, 35f),
				TARGET.apply(315f, 35f));

		// bathroom connected to the master bedroom
		Domain<Target> bathroom = domain(TARGET.apply(545f, 405f), TARGET.apply(575f, 425f), TARGET.apply(520f, 330f),
				TARGET.apply(575f, 325f), TARGET.apply(625f, 325f), TARGET.apply(525f, 375f), TARGET.apply(575f, 375f),
				TARGET.apply(625f, 375f));
		Domain<Target> bathroomShower = domain(TARGET.apply(650f, 355f), TARGET.apply(675f, 325f));
		Domain<Target> shower = domain(TARGET.apply(675f, 375f), TARGET.apply(675f, 425f), TARGET.apply(725f, 325f),
				TARGET.apply(775f, 325f), TARGET.apply(725f, 375f), TARGET.apply(775f, 375f), TARGET.apply(725f, 425f),
				TARGET.apply(775f, 425f));
		Domain<Target> bath = domain(TARGET.apply(675f, 275f), TARGET.apply(725f, 275f), TARGET.apply(775f, 275f),
				TARGET.apply(725f, 225f), TARGET.apply(775f, 225f));

		// outside living room (near top left corner)
		Domain<Target> outsideNorth = domain(TARGET.apply(162.5f, 10f));
		Domain<Target> outsideNorthEast = domain(TARGET.apply(215f, 15f), TARGET.apply(265f, 15f),
				TARGET.apply(315f, 15f), TARGET.apply(215f, 65f), TARGET.apply(265f, 65f), TARGET.apply(315f, 65f),
				TARGET.apply(315f, 35f));
		Domain<Target> outsideEast = domain(TARGET.apply(285f, 112.5f));
		Domain<Target> outsideSouthEast = domain(TARGET.apply(215f, 160f), TARGET.apply(265f, 160f),
				TARGET.apply(315f, 160f), TARGET.apply(215f, 210f), TARGET.apply(265f, 210f), TARGET.apply(315f, 210f));
		Domain<Target> outsideSouth = domain(TARGET.apply(162.5f, 215f));
		Domain<Target> outsideSouthWest = domain(TARGET.apply(15f, 160f), TARGET.apply(65f, 160f),
				TARGET.apply(115f, 160f), TARGET.apply(15f, 210f), TARGET.apply(65f, 210f), TARGET.apply(115f, 210f));
		Domain<Target> outsideWest = domain(TARGET.apply(40f, 112.5f));
		Domain<Target> outsideNorthWest = domain(TARGET.apply(15f, 15f), TARGET.apply(65f, 15f),
				TARGET.apply(115f, 15f), TARGET.apply(15f, 65f), TARGET.apply(65f, 65f), TARGET.apply(115f, 65f));
		Domain<Target> outsideChairNorth = domain(TARGET.apply(162.5f, 55f));
		Domain<Target> outsideChairEast = domain(TARGET.apply(220f, 112.5f));
		Domain<Target> outsideChairSouth = domain(TARGET.apply(162.5f, 170f));
		Domain<Target> outsideChairWest = domain(TARGET.apply(105f, 112.5f));

		// inside living room merged with the hall
		Domain<Target> insideNorth = domain(TARGET.apply(125f, 405f), TARGET.apply(125f, 365f));
		Domain<Target> insideNorthEast = domain(TARGET.apply(190f, 370f), TARGET.apply(240f, 370f),
				TARGET.apply(200f, 410f), TARGET.apply(240f, 420f));
		Domain<Target> insideEast = domain(TARGET.apply(240f, 450f));
		Domain<Target> insideSouthEast = domain(TARGET.apply(200f, 490f), TARGET.apply(240f, 480f),
				TARGET.apply(190f, 530f), TARGET.apply(240f, 530f));
		Domain<Target> insideSouth = domain(TARGET.apply(125f, 495f), TARGET.apply(125f, 535f));
		Domain<Target> insideSouthWest = domain(TARGET.apply(10f, 480f), TARGET.apply(50f, 490f),
				TARGET.apply(10f, 530f), TARGET.apply(60f, 530f));
		Domain<Target> insideWest = domain(TARGET.apply(10f, 450f));
		Domain<Target> insideNorthWest = domain(TARGET.apply(10f, 370f), TARGET.apply(10f, 420f),
				TARGET.apply(50f, 410f));

		// inside living room table (above dining room table)
		Domain<Target> chairNorthEast = domain(TARGET.apply(165f, 405f));
		Domain<Target> chairEast = domain(TARGET.apply(195f, 450f));
		Domain<Target> chairSouthEast = domain(TARGET.apply(165f, 495f));
		Domain<Target> chairSouthWest = domain(TARGET.apply(85f, 495f));
		Domain<Target> chairWest = domain(TARGET.apply(55f, 450f));
		Domain<Target> chairNorthWest = domain(TARGET.apply(90f, 405f));

		// inside living room dining table (below living room table)
		Domain<Target> insideTableWestWest = domain(TARGET.apply(35f, 255f), TARGET.apply(35f, 290f));
		Domain<Target> insideTableEast = domain(TARGET.apply(115f, 240f), TARGET.apply(165f, 250f),
				TARGET.apply(115f, 290f), TARGET.apply(165f, 290f), TARGET.apply(115f, 340f), TARGET.apply(165f, 340f));
		Domain<Target> insideTableSouth = domain(TARGET.apply(60f, 315f), TARGET.apply(60f, 345f),
				TARGET.apply(115f, 340f));
		Domain<Target> insideTableSouthWest = domain(TARGET.apply(35f, 350f), TARGET.apply(60f, 345f),
				TARGET.apply(60f, 370f));
		Domain<Target> insideTableWest = domain(TARGET.apply(60f, 235f), TARGET.apply(60f, 275f),
				TARGET.apply(60f, 315f));

		// create a graph whose heurisitic is distance
		FloorGraph<Target, Float> graph = new FloorGraph<Target, Float>((v, u) -> {
			return Math.abs(magnitude(subtract(v.position(), u.position())));
		});

		// add all domains to graph
		asList(hall, hallNorth, hallNorthNorth, hallNorthEast, hallSouth, hallSouthSouth, guestbedroom,
				guestbedroomEntrance, guestbathroom, guestbathroomShower, guestshower, kitchen, laundry, bedroomNorth,
				bedroomEast, bedroomSouth, bedroomNorthWest, bathroom, bathroomShower, shower, bath, outsideNorth,
				outsideNorthEast, outsideEast, outsideSouthEast, outsideSouth, outsideSouthWest, outsideWest,
				outsideNorthWest, outsideChairNorth, outsideChairEast, outsideChairSouth, outsideChairWest, insideNorth,
				insideNorthEast, insideEast, insideSouthEast, insideSouth, insideSouthWest, insideWest, insideNorthWest,
				chairNorthEast, chairEast, chairSouthEast, chairSouthWest, chairWest, chairNorthWest,
				insideTableWestWest, insideTableEast, insideTableSouth, insideTableSouthWest, insideTableWest)
						.forEach(graph::add);

		// apply null edge weight
		BiConsumer<Target, Target> add = (v, u) -> graph.add(v, u, null);

		// hall to guestbedroomEntrance
		add.accept(TARGET.apply(435f, 675f), TARGET.apply(500f, 685f));
		// guestbedroomEntrance to guestbedroom
		add.accept(TARGET.apply(500f, 685f), TARGET.apply(550f, 680f));
		// guestbedroom to guestbathroom
		add.accept(TARGET.apply(550f, 600f), TARGET.apply(555f, 555f));
		// hall to guestbathroom
		add.accept(TARGET.apply(435f, 525f), TARGET.apply(485f, 535f));
		// hall to kitchen
		add.accept(TARGET.apply(335f, 675f), TARGET.apply(275f, 675f));
		// kitchen to laundry
		add.accept(TARGET.apply(150f, 685f), TARGET.apply(150f, 715f));
		// bedroomSouth to bathroom
		add.accept(TARGET.apply(520f, 330f), TARGET.apply(510f, 270f));
		// bathroom to bath
		add.accept(TARGET.apply(675f, 325f), TARGET.apply(675f, 275f));
		// bedroomNorth to bedroomNorthCorner
		add.accept(TARGET.apply(610f, 35f), TARGET.apply(650f, 35f));
		// bedroomEast to bedroomEastCorner
		add.accept(TARGET.apply(650f, 185f), TARGET.apply(675f, 175f));

		// outside chairs
		add.accept(TARGET.apply(162.5f, 55f), TARGET.apply(115f, 65f));
		add.accept(TARGET.apply(162.5f, 55f), TARGET.apply(215f, 65f));
		add.accept(TARGET.apply(220f, 112.5f), TARGET.apply(215f, 65f));
		add.accept(TARGET.apply(220f, 112.5f), TARGET.apply(215f, 160f));
		add.accept(TARGET.apply(162.5f, 170f), TARGET.apply(215f, 160f));
		add.accept(TARGET.apply(162.5f, 170f), TARGET.apply(115f, 160f));
		add.accept(TARGET.apply(105f, 112.5f), TARGET.apply(115f, 160f));
		add.accept(TARGET.apply(105f, 112.5f), TARGET.apply(115f, 65f));

		// inside
		add.accept(TARGET.apply(125f, 365f), TARGET.apply(190f, 370f));
		add.accept(TARGET.apply(125f, 365f), TARGET.apply(60f, 370f));
		add.accept(TARGET.apply(240f, 450f), TARGET.apply(240f, 420f));
		add.accept(TARGET.apply(240f, 450f), TARGET.apply(240f, 480f));
		add.accept(TARGET.apply(125f, 535f), TARGET.apply(190f, 530f));
		add.accept(TARGET.apply(125f, 535f), TARGET.apply(60f, 530f));
		add.accept(TARGET.apply(10f, 450f), TARGET.apply(10f, 480f));
		add.accept(TARGET.apply(10f, 450f), TARGET.apply(10f, 420f));

		// inside chairs
		add.accept(TARGET.apply(165f, 405f), TARGET.apply(125f, 405f));
		add.accept(TARGET.apply(165f, 405f), TARGET.apply(200f, 410f));
		add.accept(TARGET.apply(195f, 450f), TARGET.apply(200f, 410f));
		add.accept(TARGET.apply(195f, 450f), TARGET.apply(200f, 490f));
		add.accept(TARGET.apply(165f, 495f), TARGET.apply(200f, 490f));
		add.accept(TARGET.apply(165f, 495f), TARGET.apply(125f, 495f));
		add.accept(TARGET.apply(85f, 495f), TARGET.apply(125f, 495f));
		add.accept(TARGET.apply(85f, 495f), TARGET.apply(50f, 490f));
		add.accept(TARGET.apply(55f, 450f), TARGET.apply(50f, 490f));
		add.accept(TARGET.apply(55f, 450f), TARGET.apply(50f, 410f));
		add.accept(TARGET.apply(90f, 405f), TARGET.apply(50f, 410f));
		add.accept(TARGET.apply(90f, 405f), TARGET.apply(125f, 405f));

		// inside to outside
		add.accept(TARGET.apply(165f, 250f), TARGET.apply(162.5f, 215f));

		// north of inside table
		add.accept(TARGET.apply(115f, 240f), TARGET.apply(60f, 235f));

		// connect hall domains
		graph.add(hall, hallNorth);
		graph.add(hall, hallSouth);
		graph.add(hallNorth, hallNorthNorth);
		graph.add(hallNorthNorth, hallNorthEast);
		graph.add(hallSouth, hallSouthSouth);

		// connect guest bathroom domains
		graph.add(guestbathroom, guestbathroomShower);
		graph.add(guestbathroomShower, guestshower);

		// connect bedroom domains
		graph.add(bedroomNorth, bedroomEast);
		graph.add(bedroomNorth, bedroomSouth);

		// connect bathroom domains
		graph.add(bathroom, bathroomShower);
		graph.add(bathroomShower, shower);

		// connect outside living room domains
		graph.add(outsideNorth, outsideNorthEast);
		graph.add(outsideNorthEast, outsideEast);
		graph.add(outsideEast, outsideSouthEast);
		graph.add(outsideSouthEast, outsideSouth);
		graph.add(outsideSouth, outsideSouthWest);
		graph.add(outsideSouthWest, outsideWest);
		graph.add(outsideWest, outsideNorthWest);
		graph.add(outsideNorthWest, outsideNorth);

		// connect living room domains
		graph.add(insideNorthEast, hallNorth);
		graph.add(insideNorthEast, hallNorthNorth);
		graph.add(insideEast, hallNorth);
		graph.add(insideEast, hall);
		graph.add(insideSouthEast, hall);

		// connect living room table domains
		graph.add(insideTableEast, hallNorth);
		graph.add(insideTableEast, hallNorthNorth);
		graph.add(insideTableEast, hallNorthEast);
		graph.add(insideTableEast, insideNorth);
		graph.add(insideTableSouthWest, insideNorthWest);
		graph.add(insideTableWest, insideTableWestWest);

		return graph;
	}

	/**
	 * Passes {@value #WIDTH}, {@value #HEIGHT}, {@value #ELLIPSE_DIAMETER}, and
	 * a new graph to super constructor.
	 */
	public IndoorEnvironment() {
		super(WIDTH, HEIGHT, ELLIPSE_DIAMETER, graph());
	}

	@Override
	public void background() {
		background(background);
	}

}
