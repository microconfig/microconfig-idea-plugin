package io.microconfig.plugin.run;

import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;

public class MicroconfigRunSettings implements RunnerSettings {

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        System.out.println("Called read external");
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        System.out.println("Called write external");
    }

}
