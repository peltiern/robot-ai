package fr.peltier.nicolas.robot.ai.test.boofcv;

    import java.awt.Point;
    import java.awt.event.KeyEvent;
    import java.awt.event.KeyListener;

    import javax.swing.JFrame;
    import javax.swing.SwingUtilities;

    import org.openimaj.feature.local.list.LocalFeatureList;
    import org.openimaj.feature.local.matcher.FastBasicKeypointMatcher;
    import org.openimaj.feature.local.matcher.MatchingUtilities;
    import org.openimaj.feature.local.matcher.consistent.ConsistentLocalFeatureMatcher2d;
    import org.openimaj.image.DisplayUtilities;
    import org.openimaj.image.FImage;
    import org.openimaj.image.MBFImage;
    import org.openimaj.image.colour.RGBColour;
    import org.openimaj.image.colour.Transforms;
    import org.openimaj.image.feature.local.engine.ipd.FinderMode;
    import org.openimaj.image.feature.local.engine.ipd.IPDSIFTEngine;
    import org.openimaj.image.feature.local.interest.HarrisIPD;
    import org.openimaj.image.feature.local.interest.IPDSelectionMode;
    import org.openimaj.image.feature.local.interest.InterestPointData;
    import org.openimaj.image.feature.local.interest.InterestPointVisualiser;
    import org.openimaj.image.feature.local.keypoints.InterestPointKeypoint;
    import org.openimaj.image.feature.local.keypoints.KeypointVisualizer;
    import org.openimaj.image.renderer.MBFImageRenderer;
    import org.openimaj.math.geometry.shape.Polygon;
    import org.openimaj.math.geometry.shape.Shape;
    import org.openimaj.math.geometry.transforms.HomographyRefinement;
    import org.openimaj.math.geometry.transforms.MatrixTransformProvider;
    import org.openimaj.math.geometry.transforms.estimation.RobustHomographyEstimator;
    import org.openimaj.math.model.fit.RANSAC;
    import org.openimaj.video.VideoDisplay;
    import org.openimaj.video.VideoDisplayListener;
    import org.openimaj.video.capture.VideoCapture;

    /**
     * OpenIMAJ Real-time (ish) SIFT tracking and matching demo
     * 
     * @author Jonathon Hare (jsh2@ecs.soton.ac.uk)
     * @author Sina Samangooei (ss@ecs.soton.ac.uk)
     */
    public class VideoIPD implements KeyListener, VideoDisplayListener<MBFImage> {
        private VideoCapture capture;
        private VideoDisplay<MBFImage> videoFrame;
        private JFrame modelFrame;
        private JFrame matchFrame;
        private MBFImage modelImage;

        private ConsistentLocalFeatureMatcher2d<InterestPointKeypoint<InterestPointData>> matcher;
        private IPDSIFTEngine engine;
        private PolygonDrawingListener polygonListener;
        private FeatureClickListener<Float[], MBFImage> featureClickListener;

        public VideoIPD() throws Exception {

            engine = getNewEngine();

            capture = new VideoCapture(320, 240);
            polygonListener = new PolygonDrawingListener();
            videoFrame = VideoDisplay.createVideoDisplay(capture);
            SwingUtilities.getRoot(videoFrame.getScreen()).addKeyListener(this);
            videoFrame.getScreen().addMouseListener(polygonListener);
            this.featureClickListener = new FeatureClickListener<Float[], MBFImage>();
            videoFrame.getScreen().addMouseListener(featureClickListener);
            // videoFrame.getScreen().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            videoFrame.addVideoListener(this);

        }

        private IPDSIFTEngine getNewEngine() {
            final int derScale = 1;
            final int intScale = 3;
            final HarrisIPD ipd = new HarrisIPD(derScale, intScale);
            engine = new IPDSIFTEngine(ipd);
            engine.setSelectionMode(new IPDSelectionMode.Threshold(10000f));
            engine.setFinderMode(new FinderMode.Characteristic<InterestPointData>());
            // engine.setSelectionMode(new IPDSelectionMode.Count(10));
            engine.setAcrossScales(true);

            return engine;
        }

        @Override
        public void keyPressed(KeyEvent key) {
            if (key.getKeyCode() == KeyEvent.VK_SPACE) {
                this.videoFrame.togglePause();
            } else if (key.getKeyChar() == 'c' && this.polygonListener.getPolygon().getVertices().size() > 2) {
                try {
                    final Polygon p = this.polygonListener.getPolygon().clone();
                    this.polygonListener.reset();
                    modelImage = capture.getCurrentFrame().process(
                            new PolygonExtractionProcessor<Float[], MBFImage>(p, RGBColour.BLACK));

                    if (modelFrame == null) {
                        modelFrame = DisplayUtilities.display(modelImage, "model");
                        modelFrame.addKeyListener(this);

                        // move the frame
                        final Point pt = modelFrame.getLocation();
                        modelFrame.setLocation(pt.x + this.videoFrame.getScreen().getWidth(), pt.y);

                        // configure the matcher
                        matcher = new ConsistentLocalFeatureMatcher2d<InterestPointKeypoint<InterestPointData>>(
                                new FastBasicKeypointMatcher<InterestPointKeypoint<InterestPointData>>(8));
                        matcher.setFittingModel(new RobustHomographyEstimator(10.0, 1500,
                                new RANSAC.PercentageInliersStoppingCondition(0.5), HomographyRefinement.NONE));
                    } else {
                        DisplayUtilities.display(modelImage, modelFrame);
                    }

                    final FImage modelF = Transforms.calculateIntensityNTSC(modelImage);
                    matcher.setModelFeatures(getNewEngine().findFeatures(modelF));
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent arg0) {
        }

        @Override
        public void keyTyped(KeyEvent arg0) {
        }

        public static void main(String[] args) throws Exception {
            new VideoIPD();
        }

        @Override
        public void afterUpdate(VideoDisplay<MBFImage> display) {
            if (matcher != null && !videoFrame.isPaused()) {
                final MBFImage capImg = videoFrame.getVideo().getCurrentFrame();
                final LocalFeatureList<InterestPointKeypoint<InterestPointData>> kpl = engine.findFeatures(Transforms
                        .calculateIntensityNTSC(capImg));

                final MBFImageRenderer renderer = capImg.createRenderer();
                renderer.drawPoints(kpl, RGBColour.MAGENTA, 3);

                MBFImage matches;
                if (matcher.findMatches(kpl)) {
                    try {
                        final Shape sh = modelImage.getBounds().transform(
                                ((MatrixTransformProvider) matcher.getModel()).getTransform().inverse());
                        renderer.drawShape(sh, 3, RGBColour.BLUE);
                    } catch (final RuntimeException e) {
                    }

                    matches = MatchingUtilities.drawMatches(modelImage, capImg, matcher.getMatches(), RGBColour.RED);
                } else {
                    matches = MatchingUtilities.drawMatches(modelImage, capImg, null, RGBColour.RED);
                }

                if (matchFrame == null) {
                    matchFrame = DisplayUtilities.display(matches, "matches");
                    matchFrame.addKeyListener(this);

                    final Point pt = matchFrame.getLocation();
                    matchFrame.setLocation(pt.x, pt.y + matchFrame.getHeight());
                } else {
                    DisplayUtilities.display(matches, matchFrame);
                }
            }
        }

        @Override
        public void beforeUpdate(MBFImage frame) {
            drawKeypoints(frame);
        }

        private void drawKeypoints(MBFImage frame) {
            final MBFImage capImg = frame;
            final LocalFeatureList<InterestPointKeypoint<InterestPointData>> kpl = engine.findFeatures(Transforms
                    .calculateIntensityNTSC(capImg));
            this.featureClickListener.setImage(kpl, frame.clone());
            final KeypointVisualizer<Float[], MBFImage> kpv = new KeypointVisualizer<Float[], MBFImage>(capImg, kpl);
            final InterestPointVisualiser<Float[], MBFImage> ipv = InterestPointVisualiser.visualiseKeypoints(
                    kpv.drawPatches(null, RGBColour.GREEN), kpl);
            frame.internalAssign(ipv.drawPatches(RGBColour.GREEN, RGBColour.BLUE));
        }
    }
