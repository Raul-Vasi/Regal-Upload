
public class Main {

	public static void main(String[] args) {
		// webclient.createResource(t, dtlBean);

		/// Ein Objekt im Repository anlegen mit apache HttpClient oder jersey
		/// Client
		// PUT /resource/:pid -d{"contentType": dtlBean.getType,
		/// "accessScheme":"....","publishScheme":"....","parentPid":""}
		// PUT /resource/:pid/metadata

		Uploader upload = new Uploader();
		upload.uploadAll();
	}

}
