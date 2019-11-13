package net.tomocraft.tweaker;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.tools.obfuscation.mcp.ObfuscationServiceMCP;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.List;

@SuppressWarnings("unused")
@IFMLLoadingPlugin.SortingIndex(1)
public class FMLLoadingPlugin implements ITweaker {
    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {

    }

    @SuppressWarnings({"JavaReflectionMemberAccess", "unchecked"})
    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        MixinBootstrap.init();

        // noinspection unchecked
        List<String> tweakClasses = (List<String>) Launch.blackboard.get("TweakClasses");

        String obfuscation = ObfuscationServiceMCP.NOTCH;
        if (tweakClasses.stream().anyMatch(s -> s.contains("net.minecraftforge.fml.common.launcher"))) {
            obfuscation = ObfuscationServiceMCP.SEARGE;
        }
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext(obfuscation);
        MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT);

        Mixins.addConfiguration("mixin.talismancounter.json");

        // Taken from Resource-Exploit-Fix by Sk1er
        CodeSource codeSource = getClass().getProtectionDomain().getCodeSource();
        try {
            Class<?> aClass = Class.forName("net.minecraftforge.fml.relauncher.CoreModManager");
            Method getIgnoredMods = null;
            try {
                getIgnoredMods = aClass.getDeclaredMethod("getIgnoredMods");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            try {
                if (getIgnoredMods == null) {
                    getIgnoredMods = aClass.getDeclaredMethod("getLoadedCoremods");
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            if (codeSource != null) {
                URL location = codeSource.getLocation();
                try {
                    File file = new File(location.toURI());
                    if (file.isFile()) {
                        try {
                            if (getIgnoredMods != null)
                                ((List<String>) getIgnoredMods.invoke(null)).remove(file.getName());
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("No CodeSource, if this is not a development environment we might run into problems!");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }
}
