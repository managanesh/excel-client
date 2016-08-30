package com.deere.isg.tx.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.SearchTerm;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by ganesh.vallabhaneni on 8/24/2016.
 */
@Component
public class IMAPSMailComponent {

    @Value("${imaps.mail.username}")
    private String username;

    @Value("${imaps.mail.password}")
    private String passkey;

    @Value("${imaps.mail.host}")//imap.gmail.com
    private String host;

    @Value("${imaps.mail.folderName}")
    private String folderName;

    @Value("${excel.source.dir}")
    String sourcePath;

    private Store store;

    @PostConstruct
    public void init() {
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        try {
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, passkey);
                }
            });

            store = session.getStore("imaps");
            store.connect(host, username, passkey);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Folder openFolder(boolean isWritable) {

        try {
            store.connect(host, username, passkey);
            Folder folder = Arrays.stream(store.getDefaultFolder().list("*"))
                    .filter(f -> f.getFullName().equalsIgnoreCase(folderName))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("#TODO"));

            folder.open(isWritable ? Folder.READ_WRITE : Folder.READ_ONLY);

            return folder;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Folder closeFolder(boolean canPersist) {

        try {
            store.connect(host, username, passkey);
            Folder folder = Arrays.stream(store.getDefaultFolder().list("*"))
                    .filter(f -> f.getFullName().equalsIgnoreCase(folderName))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("#TODO"));

            folder.close(canPersist);

            return folder;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<Message> searchBySubject(Folder folder, String partialSubject) {

        Message[] messages = new Message[1];
        try {

            messages = folder.search(new SearchTerm() {
                @Override
                public boolean match(Message msg) {
                    try {
                        return msg.getSubject().contains(partialSubject);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } /*finally {
            try {
                folder.close(false);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }*/

        return Arrays.stream(messages).collect(Collectors.toList());
    }

    public List<Message> getMailsContainsSubject(String partialSubject) {
        return searchBySubject(openFolder(true), partialSubject);

    }


    public List<MimeBodyPart> extractAttachments(String partialSubject) {

        return getMailsContainsSubject(partialSubject)
                .stream()
                .filter((msg) -> {
                    try {
                        msg.setFlag(Flags.Flag.DELETED, true);
                        return msg.getContentType().contains("multipart");
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(msg -> {
                    try {
                        return (Multipart) msg.getContent();
                    } catch (IOException | MessagingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .flatMap(mp -> {
                    try {
                        return IntStream.range(0, mp.getCount())
                                .mapToObj(index -> {
                                    try {
                                        return (MimeBodyPart) mp.getBodyPart(index);
                                    } catch (MessagingException e) {
                                        throw new RuntimeException(e);
                                    }

                                });


                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }

                })
                .filter((part -> {
                    try {
                        return Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition());
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                }))
                .collect(Collectors.toList());
                /*.collect(Collectors.toMap(part -> {
                    try {
                        return part.getFileName();
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                }, part -> {
                    try {
                        return part.getInputStream();
                    } catch (IOException | MessagingException e) {
                        throw new RuntimeException(e);
                    }
                }));
*/
    }

    public void saveAttachments(String partialSubject) {

        extractAttachments(partialSubject).forEach(part -> {
            try {
                part.saveFile(sourcePath + File.separator + part.getFileName());
            } catch (IOException | MessagingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasskey() {
        return passkey;
    }

    public void setPasskey(String passkey) {
        this.passkey = passkey;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}
