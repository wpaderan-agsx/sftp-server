package com.transfer.file.server;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.util.Properties;

@Service
public class SftpService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SftpService.class);

    @Value("${sftp.host}")
    private String sftpHost;

    @Value("${sftp.port}")
    private int sftpPort;

    @Value("${sftp.user}")
    private String sftpUser;

    @Value("${sftp.password}")
    private String sftpPassword;

    @Value("${sftp.remote.dir}")
    private String sftpRemoteDir;

    public void uploadFile(String localFilePath, String remoteFileName) throws Exception {
        Session session = null;
        ChannelSftp channelSftp = null;

        try {
            JSch jsch = new JSch();
            session = jsch.getSession(sftpUser, sftpHost, sftpPort);
            session.setPassword(sftpPassword);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect();
            System.out.println("Connected to the SFTP server.");

            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            System.out.println("Changing directory to: " + sftpRemoteDir);
            channelSftp.cd(sftpRemoteDir);

            System.out.println("Uploading file: " + localFilePath);
            FileInputStream fis = new FileInputStream(localFilePath);
            channelSftp.put(fis, remoteFileName);
            fis.close();

            System.out.println("File uploaded successfully to " + sftpRemoteDir + remoteFileName);
        } catch (Exception ex) {
            // ex.printStackTrace();
            LOGGER.error("upload service error: {}", ex.getMessage(), ex);
            throw ex;
        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }
}

