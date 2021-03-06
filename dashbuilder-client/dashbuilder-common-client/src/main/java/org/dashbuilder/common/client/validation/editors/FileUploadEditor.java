package org.dashbuilder.common.client.validation.editors;

import com.github.gwtbootstrap.client.ui.*;
import com.github.gwtbootstrap.client.ui.FileUpload;
import com.github.gwtbootstrap.client.ui.base.HasId;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.editor.client.HasEditorErrors;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.editor.ui.client.adapters.HasTextEditor;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

import java.util.List;

/**
 * <p>Editor component that wraps a gwt bootstrap file upload component an additionally provides:</p>
 * <ul>
 *     <li>Error messages - Show validation error messages.</li>     
 *     <li>Dashbuilder File Upload Servlet integration - It uses the UF Dashbuilder servlet for uploading files and provides a listener to obtain the uploaded file path.</li> 
 * </ul> 
 * <p>NOTE that for uploading the file, this editor encapsulates a FormPanel widget. So do not include it in other forms.</p>
 * 
 * <p>Usage:</p>
 * <code>
 *     @UiField     
 *     <:dash:FileUploadEditor ui:field="fileUpload"/>
 *     ...
 *     fileUpload.setCallback(new FileUploadEditor.FileUploadEditorCallback() { ... }); # Provide servlet URL and file name to create.
 *     fileUpload.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {... }); # Provide the complete handler instance.
 *     ...
 *     final String vfs_uploaded_file_path = fileUpload.getText();     
 * </code> 
 *  
 * @since 0.3.0 
 */
public class FileUploadEditor extends Composite implements
        HasId, HasText, IsEditor<HasTextEditor>, HasEditorErrors<String> {

    private static final String SCHEME = "file://";
    private static final String LOADING_IMAGE_SIZE[] = new String[] {"16px", "16px"};
    
    interface Binder extends UiBinder<Widget, FileUploadEditor> {
        Binder BINDER = GWT.create(Binder.class);
    }

    interface FileUploadEditorStyle extends CssResource {

    }

    @UiField FileUploadEditorStyle style;
    
    @UiField
    FlowPanel mainPanel;
    
    @UiField
    FormPanel formPanel;
    
    @UiField
    Tooltip errorTooltip;

    @UiField(provided = true)
    @Ignore
    com.github.gwtbootstrap.client.ui.FileUpload fileUpload;

    @UiField
    com.github.gwtbootstrap.client.ui.Image loadingImage;
    
    private String id;
    private String value;
    private HasTextEditor editor;
    private FileUploadEditorCallback callback;
    private SafeUri loadingImageUri;
    
    public interface FileUploadEditorCallback {
        String getServletUrl();
        String getPath();
    }
    
    /**
     * Constructs a FileUploadEditor.
     */
    @UiConstructor
    public FileUploadEditor() {

        fileUpload = new FileUpload() {
            @Override
            public void showErrors(final List<EditorError> errors) {
                super.showErrors(errors);
                if(errors != null && !errors.isEmpty()) {
                    for (EditorError error : errors) {
                        if(error.getEditor() == this) {
                            error.setConsumed(false);
                        }
                    }
                }
            }

            @Override
            protected void setErrorLabelText(final String errorMessage) {
                errorTooltip.setText(errorMessage);
                errorTooltip.reconfigure();
            }
        };
        
        initWidget(Binder.BINDER.createAndBindUi(this));

        // Configure file upload error displaying.
        fileUpload.setControlGroup(mainPanel);
        fileUpload.setErrorLabel(errorTooltip.asWidget());
        fileUpload.addChangeHandler(filePathChangeHandler);
        loadingImage.setVisible(false);
        
        formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
        formPanel.setMethod(FormPanel.METHOD_POST);
        formPanel.setWidget(fileUpload);
        formPanel.addSubmitCompleteHandler(formSubmitCompleteHandler);
    }
    
    private final FormPanel.SubmitCompleteHandler formSubmitCompleteHandler = new FormPanel.SubmitCompleteHandler() {
        @Override
        public void onSubmitComplete(FormPanel.SubmitCompleteEvent event) {
            if (loadingImage != null) {
                fileUpload.setVisible(true);
                loadingImage.setVisible(false);
            }
        }
    };

    public HandlerRegistration addSubmitCompleteHandler(final FormPanel.SubmitCompleteHandler submitCompleteHandler) {
        return formPanel.addSubmitCompleteHandler(submitCompleteHandler);
    }

    public void setLoadingImageUri(SafeUri loadingImageUri) {
        this.loadingImageUri = loadingImageUri;
        loadingImage.setUrl(loadingImageUri);
        loadingImage.setSize(LOADING_IMAGE_SIZE[0], LOADING_IMAGE_SIZE[1]);
    }

    public void setCallback(final FileUploadEditorCallback callback) {
        this.callback = callback;
    }

    private final ChangeHandler filePathChangeHandler = new ChangeHandler() {
        @Override
        public void onChange(ChangeEvent event) {
            final String _f = callback.getPath();
            final String _a = callback.getServletUrl() + "?path=" + SCHEME + _f;
            setText(_f);
            formPanel.setAction(_a);
            if (loadingImage != null) {
                fileUpload.setVisible(false);
                loadingImage.setVisible(true);
            }
            formPanel.submit();
        }
    };
    
    @Override
    public void showErrors(final List<EditorError> errors) {
        fileUpload.showErrors(errors);
        // TODO: Show error message label or tooltip.
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getText() {
        return value;
    }

    @Override
    public void setText(String text) {
        this.value = text;
    }

    @Override
    public HasTextEditor asEditor() {
        if (editor == null) {
            editor = HasTextEditor.of(this);
        }
        return editor;
    }
}
