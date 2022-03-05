package se.roland.example;

import biz.smartengines.smartid.swig.*;

public class Main {

    static {
        System.out.println(System.getProperty("java.library.path"));
        System.loadLibrary("jniSmartIdEngine");
    }

    public static void OutputRecognitionResult(RecognitionResult recog_result) {
        System.out.printf("Document type: %s\n", recog_result.GetDocumentType());
        System.out.printf("Match results:\n");
        MatchResultVector match_results = recog_result.GetMatchResults();
        for (int i = 0; i < match_results.size(); i++) {
            System.out.printf("    Template Type = %s\n", match_results.get(i).GetTemplateType());
            System.out.printf("    Zone = { (%f, %f), (%f, %f), (%f, %f), (%f, %f) }\n",
                    match_results.get(i).GetQuadrangle().GetPoint(0).getX(), match_results.get(i).GetQuadrangle().GetPoint(0).getY(),
                    match_results.get(i).GetQuadrangle().GetPoint(1).getX(), match_results.get(i).GetQuadrangle().GetPoint(1).getY(),
                    match_results.get(i).GetQuadrangle().GetPoint(2).getX(), match_results.get(i).GetQuadrangle().GetPoint(2).getY(),
                    match_results.get(i).GetQuadrangle().GetPoint(3).getX(), match_results.get(i).GetQuadrangle().GetPoint(3).getY());
        }

        System.out.printf("Segmentation results:\n");
        SegmentationResultVector segmentation_results = recog_result.GetSegmentationResults();
        for (int i = 0; i < segmentation_results.size(); i++) {
            StringVector raw_field_names = segmentation_results.get(i).GetRawFieldsNames();
            for (int j = 0; j < raw_field_names.size(); j++) {
                System.out.printf("    Raw field %s\n", raw_field_names.get(j));
            }
        }

        System.out.printf("String fields:\n");
        StringVector string_field_names = recog_result.GetStringFieldNames();
        for (int i = 0; i < string_field_names.size(); i++) {
            StringField field = recog_result.GetStringField(string_field_names.get(i));
            String is_accepted = field.IsAccepted() ? " [+] " : " [-] ";
            System.out.printf("    %s\t%s\t%s\n",
                    field.GetName(), is_accepted, field.GetUtf8Value());
        }

        System.out.printf("Image fields:\n");
        StringVector image_field_names = recog_result.GetImageFieldNames();
        for (int i = 0; i < image_field_names.size(); i++) {
            ImageField field = recog_result.GetImageField(image_field_names.get(i));
            String is_accepted = field.IsAccepted() ? " [+] " : " [-] ";
            System.out.printf("    %s\t%s W: %d H: %d\n",
                    field.GetName(), is_accepted,
                    field.GetValue().GetWidth(), field.GetValue().GetHeight());
        }

        System.out.printf("Result terminal: %s\n", recog_result.IsTerminal() ? " [+] " : " [-] ");
        System.out.flush();
    }

    public static void main(String[] args) {
        System.out.println(System.getProperty("java.library.path"));
        if (args.length != 2 && args.length != 3) {
            System.out.printf("Version %s. Usage: smartid_sample_java <path-to-image-file> <path-to-bundle-zip> [document-types]\n", RecognitionEngine.GetVersion());
            System.exit(-1);
        }

        String image_path = args[0];
        String config_path = args[1];
        String document_types = args.length >= 3 ? args[2] : "rus.passport.national";

        System.out.printf("SmartIDReader core version: %s\n", RecognitionEngine.GetVersion());
        System.out.printf("SmartIDReader interface version: %s\n", RecognitionEngine.GetInterfaceVersion());
        System.out.printf("image_path = %s\n", image_path);
        System.out.printf("config_path = %s\n", config_path);
        System.out.printf("document_types = %s\n", document_types);
        System.out.println();
        System.out.flush();

        OptionalStreamReporter optional_reporter = new OptionalStreamReporter();

        try {
            // ensuring interface and core versions are equal
            RecognitionEngine.AssertVersionConsistency();

            RecognitionEngine engine = new RecognitionEngine(config_path);

            SessionSettings session_settings = engine.CreateSessionSettings();

            StringVector2d supported_document_types = session_settings.GetSupportedDocumentTypes();
            System.out.printf("Supported document types groups for this configuration bundle:\n");
            for (int i = 0; i < supported_document_types.size(); ++i) {
                System.out.printf("[ ");
                for (int d = 0; d < supported_document_types.get(i).size(); ++d) {
                    System.out.printf("%s ", supported_document_types.get(i).get(d));
                }
                System.out.printf("]\n");
            }
            System.out.flush();
            supported_document_types.delete();

            // specify a concrete document type or wildcard mask
            session_settings.AddEnabledDocumentTypes(document_types);

            StringVector enabled_document_types = session_settings.GetEnabledDocumentTypes();
            System.out.printf("Spawning session with enabled document types: [ ");
            for (int d = 0; d < enabled_document_types.size(); ++d) {
                System.out.printf("%s ", enabled_document_types.get(d));
            }
            System.out.printf("]\n");
            System.out.flush();
            enabled_document_types.delete();

            RecognitionSession session = engine.SpawnSession(session_settings, optional_reporter);

            // Uses engine's internal image loading, supports format: png, jpg, jpeg, tif
            RecognitionResult result = session.ProcessImageFile(image_path);

            OutputRecognitionResult(result);

            // this is important: GC works differently with native-heap objects
            result.delete();
            session.delete();
            session_settings.delete();
            engine.delete();
        } catch (java.lang.RuntimeException e) {
            System.out.printf("Exception caught: %s\n", e.toString());
            System.out.flush();
            System.exit(-2);
        }

        System.out.printf("Processing ended\n");
        System.out.flush();
    }
}
